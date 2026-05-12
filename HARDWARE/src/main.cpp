/**
 * smart_traffic_node.ino — File chính, nạp vào Arduino IDE
 *
 * CẤU TRÚC PROJECT:
 *   smart_traffic_node/
 *   ├── smart_traffic_node.ino   ← File này (setup + loop)
 *   ├── config.h                 ← Cấu hình (WiFi, GPIO, thông số)
 *   ├── speed_sensor.h           ← Đo tốc độ IR + Moving Average
 *   └── firebase_manager.h       ← Giao tiếp Firebase
 *
 * THƯ VIỆN CẦN CÀI (Arduino IDE > Tools > Manage Libraries):
 *   1. Firebase ESP32 Client     by Mobizt       (tìm "Firebase ESP32")
 *   2. ArduinoJson               by Benoit Blanchon
 *
 * PHẦN CỨNG:
 *   ESP32 DevKit v1
 *   IR Sensor 1  → GPIO 18 (cấu hình trong config.h)
 *   IR Sensor 2  → GPIO 19
 *   (RFID, Servo, Laser → do Long xử lý, giao tiếp qua biến g_vehicleId)
 *
 * GHÉP NỐI VỚI LONG (Hardware Engineer):
 *   Long cần khai báo trong file RFID của anh ấy:
 *     volatile char g_vehicleId[32] = "UNKNOWN";
 *   và cài hàm:
 *     void triggerAlarm(float speed_kmh);   // kích Servo + Laser + Buzzer
 */

#include <Arduino.h>

#include <WiFi.h>
#include <time.h>

#include "config.h"
#include "speed_sensor.h"
#include "firebase_manager.h"

// ─────────────────────────────────────────────
//  GIAO TIẾP VỚI MODULE CỦA LONG
//  (Long định nghĩa các biến/hàm này ở file của anh ấy)
// ─────────────────────────────────────────────
// vehicleId từ RFID — Long ghi vào, Trung đọc ra
volatile char g_vehicleId[32] = "UNKNOWN";

// Hàm kích hoạt cơ cấu cảnh báo — Long implements
// Nếu chưa ghép với Long, dùng bản stub bên dưới

// ─── STUB (xóa khi ghép với code của Long) ───
void triggerAlarm(float speed_kmh) {
    DBGF("[STUB] triggerAlarm(%.2f km/h) — chờ Long implement\n", speed_kmh);
    // Long sẽ viết: xoay Servo → bật Laser → kêu Buzzer
}
// ─────────────────────────────────────────────

// ─────────────────────────────────────────────
//  ĐỐI TƯỢNG TOÀN CỤC
// ─────────────────────────────────────────────
SpeedSensor     speedSensor;
FirebaseManager firebaseMgr;
float           g_vmax_kmh = VMAX_DEFAULT_KMH;

// ─────────────────────────────────────────────
//  HÀM KẾT NỐI WIFI
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
        DBGLN("\n[WiFi] THẤT BẠI — Hệ thống chạy offline (không gửi Firebase)");
    }
}

// ─────────────────────────────────────────────
//  SETUP
// ─────────────────────────────────────────────
void setup() {
    Serial.begin(115200);
    delay(500);

    DBGLN("\n╔══════════════════════════════════════╗");
    DBGLN("║   Smart Traffic Monitor — v1.0      ║");
    DBGLN("╚══════════════════════════════════════╝");

    // 1. Khởi tạo cảm biến IR + ngắt
    speedSensor.begin();
    speedSensor.printCalibrationInfo();   // In thông số Δt hợp lệ

    // 2. Kết nối WiFi
    connectWiFi();

    // 3. Đồng bộ thời gian NTP (UTC+7 Hà Nội)
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

    // 4. Khởi tạo Firebase + lấy Vmax ban đầu
    firebaseMgr.begin();
    g_vmax_kmh = firebaseMgr.fetchVmax(VMAX_DEFAULT_KMH);

    DBGF("\n[READY] Vmax=%.1f km/h | d=%.1f cm\n", g_vmax_kmh, SENSOR_DISTANCE_CM);
    DBGLN("─────────────────────────────────────");
}

// ─────────────────────────────────────────────
//  LOOP
// ─────────────────────────────────────────────
void loop() {
    SpeedResult result;

    // ── Kiểm tra có kết quả đo mới không ──
    if (speedSensor.update(result)) {

        if (!result.valid) {
            // Kết quả không hợp lệ — in lý do, bỏ qua
            DBGF("[SKIP] %s\n", result.reason.c_str());

        } else {
            // ── In kết quả ra Serial để debug ──
            DBGLN("─────────────────────────────────────");
            DBGF("  Xe         : %s\n", (char*)g_vehicleId);
            DBGF("  Tốc độ thô : %.2f km/h\n", result.rawSpeed_kmh);
            DBGF("  Tốc độ MA  : %.2f km/h\n", result.filtSpeed_kmh);
            DBGF("  Δt         : %lu µs (%.1f ms)\n",
                 result.dt_us, result.dt_us / 1000.0f);
            DBGF("  Vmax       : %.1f km/h\n", g_vmax_kmh);

            // ── Kiểm tra vi phạm ──
            bool isViolation = (result.filtSpeed_kmh > g_vmax_kmh);

            if (isViolation) {
                DBGF("  ⚠ VI PHẠM! %.2f > %.1f km/h\n",
                     result.filtSpeed_kmh, g_vmax_kmh);
                triggerAlarm(result.filtSpeed_kmh);   // Gọi code của Long
            } else {
                DBGLN("  ✓ Bình thường");
            }
            DBGLN("─────────────────────────────────────");

            // ── Đẩy lên Firebase ──
            firebaseMgr.pushVehicleEvent(
                (const char*)g_vehicleId,
                result.filtSpeed_kmh,
                isViolation
            );
        }
    }

    // ── Polling cập nhật Vmax + ping status mỗi 30 giây ──
    static unsigned long lastPoll = 0;
    if (millis() - lastPoll > FIREBASE_POLL_INTERVAL_MS) {
        g_vmax_kmh = firebaseMgr.fetchVmax(g_vmax_kmh);
        firebaseMgr.updateSystemStatus("online");
        lastPoll   = millis();
    }

    // ── Tự reconnect WiFi nếu mất kết nối ──
    static unsigned long lastWifiCheck = 0;
    if (millis() - lastWifiCheck > 10000) {
        if (WiFi.status() != WL_CONNECTED) {
            DBGLN("[WiFi] Mất kết nối, đang thử lại...");
            WiFi.reconnect();
        }
        lastWifiCheck = millis();
    }

    delay(5);   // Nhường CPU, không block interrupt
}
