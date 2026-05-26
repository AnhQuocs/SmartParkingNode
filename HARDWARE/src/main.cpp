/**
 * main.cpp — Smart Traffic Monitor v3.0
 *
 * CẤU TRÚC PROJECT (PlatformIO):
 *   src/
 *   ├── main.cpp
 *   include/
 *   ├── config.h
 *   ├── speed_sensor.h
 *   ├── firebase_manager.h
 *   ├── rfid_reader.h
 *   └── servo_controller.h
 */

#include <Arduino.h>
#include <WiFi.h>
#include <Preferences.h>
#include <time.h>

#include "config.h"
#include "speed_sensor.h"
#include "firebase_manager.h"
#include "rfid_reader.h"
#include "servo_controller.h"

// ─────────────────────────────────────────────
//  BIẾN TOÀN CỤC
// ─────────────────────────────────────────────
volatile char   g_vehicleId[32]  = "UNKNOWN";   // RFID ghi vào đây
float           g_vmax_kmh       = VMAX_DEFAULT_KMH;
uint32_t        g_reconnectCount = 0;
float           g_cycleTime_ms   = 5.0f;
unsigned long   g_lastCycle      = 0;

// ─────────────────────────────────────────────
//  ĐỐI TƯỢNG TOÀN CỤC
// ─────────────────────────────────────────────
SpeedSensor     speedSensor;
FirebaseManager firebaseMgr;
RfidReader      rfidReader;
ServoController servo;
Preferences     prefs;

// ─────────────────────────────────────────────
//  NVS — LƯU/ĐỌC WIFI CREDENTIALS
//  Giữ WiFi đã switch sau khi reset điện
// ─────────────────────────────────────────────
void loadWifiCredentials(String &ssid, String &pass) {
    prefs.begin("wifi", true);
    ssid = prefs.getString("ssid", WIFI_SSID);       // fallback secrets.h
    pass = prefs.getString("pass", WIFI_PASSWORD);
    prefs.end();
}

void saveWifiCredentials(const String &ssid, const String &pass) {
    prefs.begin("wifi", false);
    prefs.putString("ssid", ssid);
    prefs.putString("pass", pass);
    prefs.end();
    DBGF("[Prefs] Đã lưu WiFi: %s\n", ssid.c_str());
}

// ─────────────────────────────────────────────
//  KẾT NỐI WIFI
// ─────────────────────────────────────────────
void connectWiFi() {
    String ssid, pass;
    loadWifiCredentials(ssid, pass);   // đọc NVS hoặc secrets.h

    DBGF("[WiFi] Kết nối tới '%s' ", ssid.c_str());
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid.c_str(), pass.c_str());

    uint8_t attempts = 0;
    while (WiFi.status() != WL_CONNECTED && attempts < 30) {
        delay(500);
        DBG(".");
        attempts++;
    }

    if (WiFi.status() == WL_CONNECTED) {
        DBGLN("");
        DBGF("[WiFi] OK — IP: %s | RSSI: %d dBm\n",
             WiFi.localIP().toString().c_str(), WiFi.RSSI());
    } else {
        DBGLN("\n[WiFi] THẤT BẠI — chạy offline");
    }
}

// ─────────────────────────────────────────────
//  SCAN WIFI → ĐẨY LÊN FIREBASE
// ─────────────────────────────────────────────
void scanAndPushWifi() {
    DBGLN("[WiFiScan] Đang quét...");

    int n = WiFi.scanNetworks(false, false, false, 300);
    if (n <= 0) {
        DBGLN("[WiFiScan] Không tìm thấy mạng nào.");
        return;
    }

    DBGF("[WiFiScan] Tìm thấy %d mạng\n", n);
    firebaseMgr.pushWifiScanResults(n);
    WiFi.scanDelete();
}

// ─────────────────────────────────────────────
//  CƠ CẤU CẢNH BÁO KHI VI PHẠM
// ─────────────────────────────────────────────
void triggerAlarm(float speed_kmh) {
    DBGF("[ALARM] Vi phạm %.2f km/h\n", speed_kmh);

    digitalWrite(PIN_LASER, HIGH);   // Bật laser

    servo.smoothTo(0.0f, 300);
    delay(200);
    servo.smoothTo(180.0f, 500);
    delay(200);
    servo.smoothTo(0.0f, 500);       // Kết thúc về 0°

    digitalWrite(PIN_LASER, LOW);    // Tắt laser
}

// ─────────────────────────────────────────────
//  SETUP
// ─────────────────────────────────────────────
void setup() {
    Serial.begin(115200);
    delay(500);

    DBGLN("\n╔══════════════════════════════════════════╗");
    DBGLN("║   Smart Traffic Monitor — v3.0          ║");
    DBGLN("║   IR + RFID RC522 + Servo + Laser       ║");
    DBGLN("╚══════════════════════════════════════════╝");

    // ── 1. Laser ──
    pinMode(PIN_LASER, OUTPUT);
    digitalWrite(PIN_LASER, LOW);

    // ── 2. IR Sensor ──
    speedSensor.begin();
    speedSensor.printCalibrationInfo();

    // ── 3. RFID RC522 ──
    rfidReader.begin();
    // [Quét UID lần đầu] Uncomment 2 dòng dưới để lấy UID thực tế:
    // rfidReader.scanOnlyMode();
    // while (true) { rfidReader.scanOnlyUpdate(); delay(100); }

    // ── 4. Servo MG90S ──
    servo.begin();   // Về 90° khi khởi động

    // ── 5. WiFi ──
    connectWiFi();

    // ── 6. NTP ──
    if (WiFi.status() == WL_CONNECTED) {
        configTime(7 * 3600, 0, "pool.ntp.org", "time.google.com");
        DBGLN("[NTP] Đang đồng bộ giờ...");
        delay(2000);
        struct tm ti;
        if (getLocalTime(&ti)) {
            char tbuf[30];
            strftime(tbuf, sizeof(tbuf), "%Y-%m-%d %H:%M:%S", &ti);
            DBGF("[NTP] %s\n", tbuf);
        }
    }

    // ── 7. Firebase ──
    firebaseMgr.begin();
    g_vmax_kmh = firebaseMgr.fetchVmax(VMAX_DEFAULT_KMH);

    // ── 8. Scan WiFi lần đầu ──
    scanAndPushWifi();

    DBGLN("\n─────────────────────────────────────────");
    DBGF("[READY] Vmax=%.1f km/h | d=%.1f cm | GPIO Servo=%d | Laser=%d\n",
         g_vmax_kmh, SENSOR_DISTANCE_CM, PIN_SERVO, PIN_LASER);
    DBGLN("[READY] Quẹt thẻ RFID → lái xe qua 2 cảm biến IR");
    DBGLN("─────────────────────────────────────────");
}

// ─────────────────────────────────────────────
//  LOOP
// ─────────────────────────────────────────────
void loop() {

    // ── Tính cycle time ──
    g_cycleTime_ms = (float)(millis() - g_lastCycle);
    g_lastCycle    = millis();

    // ── 1. Đọc RFID ──
    rfidReader.update();

    // ── 2. Đo tốc độ ──
    SpeedResult result;
    if (speedSensor.update(result)) {
        if (!result.valid) {
            DBGF("[SKIP] %s\n", result.reason.c_str());
        } else {
            DBGLN("─────────────────────────────────────────");
            DBGF("  Xe         : %s\n",   (char*)g_vehicleId);
            DBGF("  Tốc độ thô : %.2f km/h\n", result.rawSpeed_kmh);
            DBGF("  Tốc độ MA  : %.2f km/h\n", result.filtSpeed_kmh);
            DBGF("  Δt         : %lu µs (%.1f ms)\n",
                 result.dt_us, result.dt_us / 1000.0f);
            DBGF("  Vmax       : %.1f km/h\n", g_vmax_kmh);

            bool isViolation = (result.filtSpeed_kmh > g_vmax_kmh);
            if (isViolation) {
                DBGF("  ⚠  VI PHẠM! %.2f > %.1f km/h\n",
                     result.filtSpeed_kmh, g_vmax_kmh);
                triggerAlarm(result.filtSpeed_kmh);
            } else {
                DBGLN("  ✓  Bình thường");
            }
            DBGLN("─────────────────────────────────────────");

            firebaseMgr.pushVehicleEvent(
                (const char*)g_vehicleId,
                result.filtSpeed_kmh,
                isViolation);
        }
    }

    // ── 3. Polling Vmax + ping mỗi 30 giây ──
    static unsigned long lastPoll = 0;
    if (millis() - lastPoll > FIREBASE_POLL_INTERVAL_MS) {
        g_vmax_kmh = firebaseMgr.fetchVmax(g_vmax_kmh);
        firebaseMgr.updateSystemStatus("online");
        lastPoll = millis();
    }

    // ── 4. Reconnect WiFi nếu mất kết nối ──
    static unsigned long lastWifiCheck = 0;
    if (millis() - lastWifiCheck > 10000) {
        if (WiFi.status() != WL_CONNECTED) {
            DBGLN("[WiFi] Mất kết nối — đang thử lại...");
            WiFi.reconnect();
            g_reconnectCount++;
        }
        lastWifiCheck = millis();
    }

    // ── 5. System monitor mỗi 10 giây ──
    static unsigned long lastMonitor = 0;
    if (millis() - lastMonitor > 10000) {
        bool ir1_ok  = (digitalRead(PIN_IR1) != -1);
        bool ir2_ok  = (digitalRead(PIN_IR2) != -1);
        bool rfid_ok = (strlen((char*)g_vehicleId) > 0);

        firebaseMgr.pushSystemMonitor(g_reconnectCount, g_cycleTime_ms);
        firebaseMgr.pushSensorStatus(ir1_ok, ir2_ok, rfid_ok, g_cycleTime_ms);
        firebaseMgr.pushConnectionStatus();
        lastMonitor = millis();
    }

    // ── 6. Switch WiFi từ app mỗi 5 giây ──
    //  Flow: fetchNetworkConfig đọc credentials KHI VẪN ONLINE
    //        performNetworkSwitch ghi SWITCHING → disconnect → connect
    //        Nếu thất bại → fallback NVS/secrets.h
    static unsigned long lastNetCheck = 0;
    if (millis() - lastNetCheck > 5000) {
        String newSsid, newPass;
        if (firebaseMgr.fetchNetworkConfig(newSsid, newPass)) {
            bool ok = firebaseMgr.performNetworkSwitch(newSsid, newPass);
            if (ok) {
                // Lưu vào NVS để giữ sau khi reset
                saveWifiCredentials(newSsid, newPass);
            }
        }
        lastNetCheck = millis();
    }

    // ── 7. Auto scan WiFi mỗi 60 giây ──
    static unsigned long lastScan = 0;
    if (millis() - lastScan > 60000) {
        scanAndPushWifi();
        lastScan = millis();
    }

    delay(5);   // Nhường CPU, không block ngắt IR
}