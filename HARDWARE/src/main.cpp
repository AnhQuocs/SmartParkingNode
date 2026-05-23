/**
 * smart_traffic_node.ino — v3.0
 *
 * CẤU TRÚC PROJECT:
 *   smart_traffic_node/
 *   ├── smart_traffic_node.ino   ← File này
 *   ├── config.h                 ← Cấu hình GPIO, WiFi, thông số
 *   ├── speed_sensor.h           ← Đo tốc độ IR + Moving Average
 *   ├── firebase_manager.h       ← Giao tiếp Firebase Realtime DB
 *   ├── rfid_reader.h            ← Đọc thẻ RFID RC522
 *   └── servo_controller.h       ← Điều khiển Servo MG90S
 *
 * THƯ VIỆN CẦN CÀI (Arduino IDE > Tools > Manage Libraries):
 *   1. Firebase ESP32 Client  by Mobizt
 *   2. ArduinoJson            by Benoit Blanchon
 *   3. MFRC522                by GithubCommunity
 *   (Servo dùng LEDC built-in của ESP32 — không cần cài thêm)
 *
 * SƠ ĐỒ CHÂN TỔNG HỢP:
 *   GPIO 35  → IR Sensor 1       (+ pull-up ngoài 10kΩ lên 3.3V)
 *   GPIO 33  → IR Sensor 2
 *   GPIO 18  → RC522 SCK
 *   GPIO 19  → RC522 MISO
 *   GPIO 23  → RC522 MOSI
 *   GPIO  5  → RC522 SDA/CS
 *   GPIO  4  → RC522 RST
 *   GPIO 13  → Servo MG90S Signal
 *
 * LUỒNG HOẠT ĐỘNG:
 *   1. Xe tiến vào → RC522 đọc thẻ → ghi g_vehicleId
 *   2. Xe qua IR1 → IR2 → tính tốc độ
 *   3. Nếu vượt Vmax → servo xoay + (laser + buzzer nếu có)
 *   4. Đẩy dữ liệu lên Firebase
 *   5. Servo về vị trí chờ
 */

#include <Arduino.h>
#include <WiFi.h>
#include <time.h>

#include "config.h"
#include "speed_sensor.h"
#include "firebase_manager.h"
#include "rfid_reader.h"
#include "servo_controller.h"

// ─────────────────────────────────────────────
//  BIẾN CHIA SẺ TOÀN CỤC
//  rfid_reader.h ghi vào đây qua extern
// ─────────────────────────────────────────────
volatile char g_vehicleId[32] = "UNKNOWN";

// ─────────────────────────────────────────────
//  ĐỐI TƯỢNG TOÀN CỤC
// ─────────────────────────────────────────────
SpeedSensor     speedSensor;
FirebaseManager firebaseMgr;
RfidReader      rfidReader;
ServoController servo;
float           g_vmax_kmh = VMAX_DEFAULT_KMH;

// ─────────────────────────────────────────────
//  CƠ CẤU CẢNH BÁO
//  Servo đã implement; Laser/Buzzer để TODO khi có phần cứng
// ─────────────────────────────────────────────
void triggerAlarm(float speed_kmh) {
    DBGF("[ALARM] Vi phạm %.2f km/h — kích hoạt cảnh báo\n", speed_kmh);

    digitalWrite(PIN_LASER, HIGH);

    servo.smoothTo(0.0f, 300);
    delay(200);
    servo.smoothTo(180.0f, 500);
    delay(200);
    servo.smoothTo(0.0f, 500);

    digitalWrite(PIN_LASER, LOW);
}

// ─────────────────────────────────────────────
//  KẾT NỐI WIFI
// ─────────────────────────────────────────────
void connectWiFi() {
    DBGF("[WiFi] Kết nối tới '%s' ", WIFI_SSID);
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
        DBGF("[WiFi] OK — IP: %s | RSSI: %d dBm\n",
             WiFi.localIP().toString().c_str(), WiFi.RSSI());
    } else {
        DBGLN("\n[WiFi] THẤT BẠI — hệ thống chạy offline, không gửi Firebase");
    }
}

// ─────────────────────────────────────────────
//  SETUP
// ─────────────────────────────────────────────
void setup() {
    Serial.begin(115200);
    delay(500);

    DBGLN("\n╔══════════════════════════════════════════╗");
    DBGLN("║   Smart Traffic Monitor — v3.0          ║");
    DBGLN("║   IR + RFID RC522 + Servo MG90S         ║");
    DBGLN("╚══════════════════════════════════════════╝");

    // ── 1. IR Sensor ──
    speedSensor.begin();
    speedSensor.printCalibrationInfo();

    pinMode(PIN_LASER, OUTPUT);
    digitalWrite(PIN_LASER, LOW);

    // ── 2. RFID RC522 ──
    rfidReader.begin();
    // [Quét UID lần đầu] Uncomment 2 dòng dưới, nạp lên,
    // đặt thẻ vào đầu đọc, chép UID từ Serial Monitor,
    // rồi comment lại và điền vào VEHICLE_TABLE trong rfid_reader.h
    // rfidReader.scanOnlyMode();
    // while (true) { rfidReader.scanOnlyUpdate(); delay(100); }

    // ── 3. Servo MG90S ──
    servo.begin();   // Tự động về 90° khi khởi động

    // ── 4. WiFi ──
    connectWiFi();

    // ── 5. Đồng bộ giờ NTP (UTC+7 Hà Nội) ──
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

    // ── 6. Firebase — khởi tạo và lấy Vmax hiện tại ──
    firebaseMgr.begin();
    g_vmax_kmh = firebaseMgr.fetchVmax(VMAX_DEFAULT_KMH);

    DBGLN("\n─────────────────────────────────────────");
    DBGF("[READY] Vmax=%.1f km/h | d=%.1f cm | Servo GPIO%d\n",
         g_vmax_kmh, SENSOR_DISTANCE_CM, PIN_SERVO);
    DBGLN("[READY] Quẹt thẻ RFID → lái xe qua 2 cảm biến IR");
    DBGLN("─────────────────────────────────────────");
}

// ─────────────────────────────────────────────
//  LOOP
// ─────────────────────────────────────────────
void loop() {

    // ── 1. Đọc RFID liên tục ──
    // rfidReader.update() tự ghi vào g_vehicleId khi có thẻ mới
    rfidReader.update();

    // ── 2. Xử lý kết quả đo tốc độ ──
    SpeedResult result;
    if (speedSensor.update(result)) {

        if (!result.valid) {
            // Kết quả không hợp lệ (nhiễu, timeout, v.v.)
            DBGF("[SKIP] %s\n", result.reason.c_str());

        } else {
            // In kết quả ra Serial để debug
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

            // Đẩy sự kiện lên Firebase
            firebaseMgr.pushVehicleEvent(
                (const char*)g_vehicleId,
                result.filtSpeed_kmh,
                isViolation
            );

            // [Tuỳ chọn] Reset vehicleId sau mỗi lần đo
            // Bỏ comment nếu mỗi lần qua trạm đều phải quẹt thẻ mới
            // rfidReader.resetVehicleId();
        }
    }

    // ── 3. Polling Firebase mỗi 30 giây ──
    // Cập nhật Vmax từ app + gửi ping trạng thái "online"
    static unsigned long lastPoll = 0;
    if (millis() - lastPoll > FIREBASE_POLL_INTERVAL_MS) {
        g_vmax_kmh = firebaseMgr.fetchVmax(g_vmax_kmh);
        firebaseMgr.updateSystemStatus("online");
        lastPoll = millis();
    }

    // ── 4. Tự reconnect WiFi nếu mất kết nối ──
    static unsigned long lastWifiCheck = 0;
    if (millis() - lastWifiCheck > 10000) {
        if (WiFi.status() != WL_CONNECTED) {
            DBGLN("[WiFi] Mất kết nối — đang thử lại...");
            WiFi.reconnect();
        }
        lastWifiCheck = millis();
    }

    delay(5);   // Nhường CPU, không block ngắt IR
}
