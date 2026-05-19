/**
 * smart_traffic_node.ino — v2.0 (tích hợp RFID RC522)
 *
 * CẤU TRÚC PROJECT:
 *   smart_traffic_node/
 *   ├── smart_traffic_node.ino   ← File này
 *   ├── config.h                 ← Cấu hình (WiFi, GPIO, thông số)
 *   ├── speed_sensor.h           ← Đo tốc độ IR + Moving Average
 *   ├── firebase_manager.h       ← Giao tiếp Firebase
 *   └── rfid_reader.h            ← ĐỌC THẺ RFID RC522 ← MỚI
 *
 * THƯ VIỆN CẦN CÀI (Arduino IDE > Tools > Manage Libraries):
 *   1. Firebase ESP32 Client  by Mobizt
 *   2. ArduinoJson            by Benoit Blanchon
 *   3. MFRC522                by GithubCommunity   ← MỚI
 *
 * ĐẤU NỐI RC522:
 *   RC522 VCC  → 3.3V (KHÔNG 5V!)
 *   RC522 GND  → GND
 *   RC522 SDA  → GPIO 5
 *   RC522 SCK  → GPIO 18
 *   RC522 MOSI → GPIO 23
 *   RC522 MISO → GPIO 19
 *   RC522 RST  → GPIO 4
 *
 * ĐẤU NỐI IR (đã đổi chân):
 *   IR Sensor 1 → GPIO 25  (trước: GPIO 18)
 *   IR Sensor 2 → GPIO 26  (trước: GPIO 19)
 *
 * LUỒNG HOẠT ĐỘNG:
 *   1. Xe tiến vào → RC522 đọc thẻ → ghi vào g_vehicleId
 *   2. Xe đi qua IR1 → IR2 → tính tốc độ
 *   3. Kiểm tra vi phạm → kích alrm nếu cần
 *   4. Đẩy {vehicleId, speed, isViolation} lên Firebase
 *   5. Reset g_vehicleId → UNKNOWN cho xe tiếp theo
 */

#include <Arduino.h>
#include <WiFi.h>
#include <time.h>

#include "config.h"
#include "speed_sensor.h"
#include "firebase_manager.h"
#include "rfid_reader.h"   // ← MỚI

// ─────────────────────────────────────────────
//  BIẾN CHIA SẺ
// ─────────────────────────────────────────────
// RfidReader ghi vào đây, main đọc ra để đẩy Firebase
volatile char g_vehicleId[32] = "UNKNOWN";

// ─────────────────────────────────────────────
//  CƠ CẤU CẢNH BÁO (stub — Long implement)
// ─────────────────────────────────────────────
void triggerAlarm(float speed_kmh) {
    DBGF("[STUB] triggerAlarm(%.2f km/h) — chờ Long implement\n", speed_kmh);
    // Long viết: xoay Servo → bật Laser → kêu Buzzer
}

// ─────────────────────────────────────────────
//  ĐỐI TƯỢNG TOÀN CỤC
// ─────────────────────────────────────────────
SpeedSensor     speedSensor;
FirebaseManager firebaseMgr;
RfidReader      rfidReader;    // ← MỚI
float           g_vmax_kmh = VMAX_DEFAULT_KMH;

// ─────────────────────────────────────────────
//  WIFI
// ─────────────────────────────────────────────
void connectWiFi() {
    DBGF("[WiFi] Kết nối tới '%s'", WIFI_SSID);
    WiFi.mode(WIFI_STA);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    uint8_t attempts = 0;
    while (WiFi.status() != WL_CONNECTED && attempts < 30) {
        delay(500);
        DBG(".");
        attempts++;
    }

    if (WiFi.status() == WL_CONNECTED) {
        DBGLN("");
        DBGF("[WiFi] IP: %s | RSSI: %d dBm\n",
             WiFi.localIP().toString().c_str(), WiFi.RSSI());
    } else {
        DBGLN("\n[WiFi] THẤT BẠI — chạy offline");
    }
}

// ─────────────────────────────────────────────
//  SETUP
// ─────────────────────────────────────────────
void setup() {
    Serial.begin(115200);
    delay(500);

    DBGLN("\n╔══════════════════════════════════════╗");
    DBGLN("║   Smart Traffic Monitor — v2.0      ║");
    DBGLN("║   + RFID RC522 Integration          ║");
    DBGLN("╚══════════════════════════════════════╝");

    // 1. Cảm biến IR
    speedSensor.begin();
    speedSensor.printCalibrationInfo();

    // 2. RFID RC522 — khởi tạo SPI và module
    rfidReader.begin();

    // ── Nếu muốn chỉ quét UID để điền bảng xe ──
    // Uncomment 2 dòng sau, nạp lên, mở Serial Monitor,
    // đặt từng thẻ vào đầu đọc để xem UID, rồi comment lại:
    // rfidReader.scanOnlyMode();
    // while(true) { rfidReader.scanOnlyUpdate(); delay(100); }
    // ─────────────────────────────────────────

    // 3. WiFi
    connectWiFi();

    // 4. NTP
    if (WiFi.status() == WL_CONNECTED) {
        configTime(7 * 3600, 0, "pool.ntp.org", "time.google.com");
        DBGLN("[NTP] Đang đồng bộ giờ...");
        delay(2000);
        struct tm ti;
        if (getLocalTime(&ti)) {
            char tbuf[30];
            strftime(tbuf, sizeof(tbuf), "%Y-%m-%d %H:%M:%S", &ti);
            DBGF("[NTP] Giờ hiện tại: %s\n", tbuf);
        }
    }

    // 5. Firebase
    firebaseMgr.begin();
    g_vmax_kmh = firebaseMgr.fetchVmax(VMAX_DEFAULT_KMH);

    DBGF("\n[READY] Vmax=%.1f km/h | d=%.1f cm\n", g_vmax_kmh, SENSOR_DISTANCE_CM);
    DBGLN("[READY] Đặt thẻ RFID vào đầu đọc, sau đó lái xe qua cảm biến IR.");
    DBGLN("─────────────────────────────────────");
}

// ─────────────────────────────────────────────
//  LOOP
// ─────────────────────────────────────────────
void loop() {

    // ── 1. Cập nhật RFID liên tục ──
    // Mỗi khi có thẻ mới → tự động cập nhật g_vehicleId
    rfidReader.update();

    // ── 2. Kiểm tra kết quả đo tốc độ ──
    SpeedResult result;
    if (speedSensor.update(result)) {

        if (!result.valid) {
            DBGF("[SKIP] %s\n", result.reason.c_str());

        } else {
            DBGLN("─────────────────────────────────────");
            DBGF("  Xe         : %s\n", (char*)g_vehicleId);
            DBGF("  Tốc độ thô : %.2f km/h\n", result.rawSpeed_kmh);
            DBGF("  Tốc độ MA  : %.2f km/h\n", result.filtSpeed_kmh);
            DBGF("  Δt         : %lu µs (%.1f ms)\n",
                 result.dt_us, result.dt_us / 1000.0f);
            DBGF("  Vmax       : %.1f km/h\n", g_vmax_kmh);

            bool isViolation = (result.filtSpeed_kmh > g_vmax_kmh);

            if (isViolation) {
                DBGF("  ⚠ VI PHẠM! %.2f > %.1f km/h\n",
                     result.filtSpeed_kmh, g_vmax_kmh);
                triggerAlarm(result.filtSpeed_kmh);
            } else {
                DBGLN("  ✓ Bình thường");
            }
            DBGLN("─────────────────────────────────────");

            // ── 3. Đẩy lên Firebase (kèm vehicleId thực) ──
            firebaseMgr.pushVehicleEvent(
                (const char*)g_vehicleId,
                result.filtSpeed_kmh,
                isViolation
            );

            // ── 4. Reset vehicleId sau khi ghi xong ──
            // Bỏ comment dòng dưới nếu muốn reset sau mỗi lần đo
            // (phù hợp khi xe phải quẹt thẻ mỗi lần qua trạm)
            // rfidReader.resetVehicleId();
        }
    }

    // ── 5. Polling Firebase mỗi 30 giây ──
    static unsigned long lastPoll = 0;
    if (millis() - lastPoll > FIREBASE_POLL_INTERVAL_MS) {
        g_vmax_kmh = firebaseMgr.fetchVmax(g_vmax_kmh);
        firebaseMgr.updateSystemStatus("online");
        lastPoll   = millis();
    }

    // ── 6. Reconnect WiFi nếu mất ──
    static unsigned long lastWifiCheck = 0;
    if (millis() - lastWifiCheck > 10000) {
        if (WiFi.status() != WL_CONNECTED) {
            DBGLN("[WiFi] Mất kết nối, đang thử lại...");
            WiFi.reconnect();
        }
        lastWifiCheck = millis();
    }

    delay(5);
}
