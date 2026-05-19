/**
 * config.h — Toàn bộ thông số cấu hình hệ thống
 * SỬA FILE NÀY trước khi nạp firmware, không cần đụng file khác
 *
 * THAY ĐỔI so với v1.0:
 *   - Thêm cấu hình chân SPI cho RFID RC522
 *   - Dời PIN_IR1 từ 18→25, PIN_IR2 từ 19→26
 *     (18 và 19 bị chiếm bởi SPI SCK và MISO của RC522)
 *   - Thêm RFID_DEBOUNCE_MS
 */

#ifndef CONFIG_H
#define CONFIG_H

// ═══════════════════════════════════════════════
//  WIFI
// ═══════════════════════════════════════════════
#define WIFI_SSID       "Đức"
#define WIFI_PASSWORD   "77777777@"

// ═══════════════════════════════════════════════
//  FIREBASE
// ═══════════════════════════════════════════════
#define FIREBASE_HOST   "smarttrafficradar-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH   "jrv3pgHgTUqgAM5vQPICAw4xs3ZwVRvc86qFqlzc"

// ID của trạm radar này
#define NODE_ID         "radar_node_01"

// ═══════════════════════════════════════════════
//  CHÂN GPIO ESP32 — IR SENSOR
//  ⚠️ Đã dời từ 18/19 → 25/26 để tránh xung đột SPI (RC522)
// ═══════════════════════════════════════════════
#define PIN_IR1     35
#define PIN_IR2     33

// ═══════════════════════════════════════════════
//  CHÂN GPIO ESP32 — RFID RC522 (SPI)
//  SCK  → GPIO 18  (SPI mặc định ESP32, tự động)
//  MOSI → GPIO 23  (SPI mặc định ESP32, tự động)
//  MISO → GPIO 19  (SPI mặc định ESP32, tự động)
// ═══════════════════════════════════════════════
#define PIN_RFID_SS     5    // SDA/CS của RC522
#define PIN_RFID_RST    4    // RST của RC522

// ═══════════════════════════════════════════════
//  RFID — CHỐNG ĐỌC LẶP
// ═══════════════════════════════════════════════
// Thời gian tối thiểu giữa 2 lần đọc thẻ (ms)
// Tránh đọc 1 thẻ nhiều lần liên tiếp khi xe đứng yên
#define RFID_DEBOUNCE_MS   2000   // 2 giây

// ═══════════════════════════════════════════════
//  THÔNG SỐ VẬT LÝ
// ═══════════════════════════════════════════════
#define SENSOR_DISTANCE_CM   30.0f
#define SENSOR_DISTANCE_M    (SENSOR_DISTANCE_CM / 100.0f)

// ═══════════════════════════════════════════════
//  NGƯỠNG TỐC ĐỘ
// ═══════════════════════════════════════════════
#define VMAX_DEFAULT_KMH    20.0f

// ═══════════════════════════════════════════════
//  MOVING AVERAGE
// ═══════════════════════════════════════════════
#define MA_WINDOW_SIZE   5

// ═══════════════════════════════════════════════
//  GIỚI HẠN TỐC ĐỘ HỢP LỆ
// ═══════════════════════════════════════════════
#define SPEED_MIN_KMH    1.0f
#define SPEED_MAX_KMH    120.0f

// ═══════════════════════════════════════════════
//  TIMEOUT
// ═══════════════════════════════════════════════
#define MEASUREMENT_TIMEOUT_MS   8000

// ═══════════════════════════════════════════════
//  THỜI GIAN POLLING FIREBASE
// ═══════════════════════════════════════════════
#define FIREBASE_POLL_INTERVAL_MS   30000

// ═══════════════════════════════════════════════
//  DEBUG
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
