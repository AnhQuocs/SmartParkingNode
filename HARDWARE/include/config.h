/**
 * config.h — Toàn bộ thông số cấu hình hệ thống
 * SỬA FILE NÀY trước khi nạp firmware, không cần đụng file khác
 */

#ifndef CONFIG_H
#define CONFIG_H

// ═══════════════════════════════════════════════
//  WIFI
// ═══════════════════════════════════════════════
#define WIFI_SSID       "Trung Bí"
#define WIFI_PASSWORD   "1234567890"

// ═══════════════════════════════════════════════
//  FIREBASE
// ═══════════════════════════════════════════════
#define FIREBASE_HOST   "smarttrafficradar-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH   "jrv3pgHgTUqgAM5vQPICAw4xs3ZwVRvc86qFqlzc"   // Firebase > Project Settings > Service Accounts

// ID của trạm radar này — khớp với key trong Firebase
// Ví dụ: "radar_node_01", "radar_node_02", ...
#define NODE_ID         "radar_node_01"

// ═══════════════════════════════════════════════
//  CHÂN GPIO ESP32
// ═══════════════════════════════════════════════
#define PIN_IR1     32   // IR cảm biến 1 (xe vào trước)
#define PIN_IR2     33    // IR cảm biến 2 (xe vào sau)

// ═══════════════════════════════════════════════
//  THÔNG SỐ VẬT LÝ — ĐO THỰC TẾ RỒI ĐIỀN VÀO
// ═══════════════════════════════════════════════
// Dùng thước đo khoảng cách giữa tâm 2 cảm biến IR (đơn vị: mét)
// Ví dụ: 2 cảm biến cách nhau 30cm → 0.30f
#define SENSOR_DISTANCE_CM   30.0f              // cm — sửa theo thực tế
#define SENSOR_DISTANCE_M    (SENSOR_DISTANCE_CM / 100.0f)

// ═══════════════════════════════════════════════
//  NGƯỠNG TỐC ĐỘ
// ═══════════════════════════════════════════════
#define VMAX_DEFAULT_KMH    20.0f   // km/h — mặc định, có thể cập nhật từ Firebase

// ═══════════════════════════════════════════════
//  MOVING AVERAGE
// ═══════════════════════════════════════════════
// Số mẫu trong bộ lọc. Tăng → mượt hơn nhưng phản ứng chậm hơn
// Khuyến nghị: 3 (thực tế nhanh) hoặc 5 (ổn định hơn)
#define MA_WINDOW_SIZE   5

// ═══════════════════════════════════════════════
//  GIỚI HẠN TỐC ĐỘ HỢP LỆ
// ═══════════════════════════════════════════════
// Loại bỏ kết quả vô lý (nhiễu hoặc xe qua quá chậm/nhanh)
#define SPEED_MIN_KMH    1.0f     // Dưới mức này: bỏ qua (người đi bộ, dừng)
#define SPEED_MAX_KMH    120.0f   // Trên mức này: bỏ qua (nhiễu xung đột IR)

// ═══════════════════════════════════════════════
//  TIMEOUT
// ═══════════════════════════════════════════════
// Xe qua IR1 nhưng chưa tới IR2 sau X ms → huỷ phiên đo
#define MEASUREMENT_TIMEOUT_MS   8000   // 8 giây

// ═══════════════════════════════════════════════
//  THỜI GIAN POLLING FIREBASE (cập nhật Vmax từ app)
// ═══════════════════════════════════════════════
#define FIREBASE_POLL_INTERVAL_MS   30000   // 30 giây

// ═══════════════════════════════════════════════
//  DEBUG — đặt 0 để tắt Serial khi triển khai thật
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
