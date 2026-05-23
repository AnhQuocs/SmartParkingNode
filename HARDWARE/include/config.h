/**
 * config.h — Toàn bộ thông số cấu hình hệ thống
 * SỬA FILE NÀY trước khi nạp firmware, không cần đụng file khác
 *
 * v3.0 — Tích hợp: IR Sensor + RFID RC522 + Servo MG90S
 *
 * BẢNG CHÂN GPIO TỔNG HỢP:
 *   GPIO 35  → IR Sensor 1  (input-only, cần pull-up ngoài 10kΩ)
 *   GPIO 33  → IR Sensor 2
 *   GPIO 18  → RC522 SCK   (SPI mặc định)
 *   GPIO 19  → RC522 MISO  (SPI mặc định)
 *   GPIO 23  → RC522 MOSI  (SPI mặc định)
 *   GPIO  5  → RC522 SDA/CS
 *   GPIO  4  → RC522 RST
 *   GPIO 13  → Servo MG90S Signal (PWM)
 */

#ifndef CONFIG_H
#define CONFIG_H

#include "secrets.h"

#define NODE_ID         "radar_node_01"

// ═══════════════════════════════════════════════
//  CHÂN GPIO — IR SENSOR
//  ⚠️  GPIO 35 là input-only trên ESP32 DevKit v1:
//      KHÔNG có pull-up nội, KHÔNG thể dùng INPUT_PULLUP
//      → Gắn điện trở 10kΩ từ GPIO 35 lên 3.3V bên ngoài
//  GPIO 33 dùng INPUT_PULLUP bình thường
// ═══════════════════════════════════════════════
#define PIN_IR1     35   // IR Sensor 1 — xe vào trước
#define PIN_IR2     33   // IR Sensor 2 — xe vào sau

// ═══════════════════════════════════════════════
//  CHÂN GPIO — RFID RC522 (SPI bus)
//  SCK  → GPIO 18  |  MOSI → GPIO 23  |  MISO → GPIO 19
//  (Ba chân trên là SPI mặc định ESP32, không khai báo thêm)
// ═══════════════════════════════════════════════
#define PIN_RFID_SS     5    // SDA / CS của RC522
#define PIN_RFID_RST    22    // RST của RC522

// ═══════════════════════════════════════════════
//  CHÂN GPIO — SERVO MG90S
//  Dây cam/vàng → GPIO 13  (PWM signal)
//  Dây đỏ       → 5V / VIN  (KHÔNG dùng 3.3V)
//  Dây nâu/đen  → GND
//  ⚡ Khuyến nghị: tụ 100µF giữa VIN và GND gần servo
// ═══════════════════════════════════════════════
#define PIN_SERVO   14
#define PIN_LASER   26

// ═══════════════════════════════════════════════
//  RFID — CHỐNG ĐỌC LẶP
// ═══════════════════════════════════════════════
#define RFID_DEBOUNCE_MS   2000   // ms — bỏ qua đọc lại trong 2 giây

// ═══════════════════════════════════════════════
//  THÔNG SỐ VẬT LÝ — ĐO THỰC TẾ RỒI ĐIỀN
// ═══════════════════════════════════════════════
#define SENSOR_DISTANCE_CM   30.0f
#define SENSOR_DISTANCE_M    (SENSOR_DISTANCE_CM / 100.0f)

// ═══════════════════════════════════════════════
//  NGƯỠNG TỐC ĐỘ
// ═══════════════════════════════════════════════
#define VMAX_DEFAULT_KMH    20.0f   // km/h — có thể cập nhật từ Firebase

// ═══════════════════════════════════════════════
//  MOVING AVERAGE FILTER
// ═══════════════════════════════════════════════
#define MA_WINDOW_SIZE   5   // Tăng → mượt hơn, phản ứng chậm hơn

// ═══════════════════════════════════════════════
//  GIỚI HẠN TỐC ĐỘ HỢP LỆ (lọc nhiễu IR)
// ═══════════════════════════════════════════════
#define SPEED_MIN_KMH    1.0f     // Dưới mức này: bỏ qua (người đi bộ)
#define SPEED_MAX_KMH    120.0f   // Trên mức này: bỏ qua (nhiễu IR)

// ═══════════════════════════════════════════════
//  TIMEOUT ĐO TỐC ĐỘ
// ═══════════════════════════════════════════════
#define MEASUREMENT_TIMEOUT_MS   8000   // ms — huỷ nếu IR2 không kích trong 8s

// ═══════════════════════════════════════════════
//  POLLING FIREBASE (cập nhật Vmax từ app)
// ═══════════════════════════════════════════════
#define FIREBASE_POLL_INTERVAL_MS   30000   // ms — 30 giây

// ═══════════════════════════════════════════════
//  DEBUG — đặt 0 khi triển khai thật để tắt Serial
// ═══════════════════════════════════════════════
#define DEBUG_ENABLED   1

#if DEBUG_ENABLED
  #define DBG(x)    Serial.print(x)
  #define DBGLN(x)  Serial.println(x)
  #define DBGF(...) Serial.printf(__VA_ARGS__)
#else
  #define DBG(x)
  #define DBGLN(x)
  #define DBGF(...)
#endif

#endif // CONFIG_H
