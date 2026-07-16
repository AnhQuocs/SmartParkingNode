#pragma once


#define PIN_RFID_SS 5
#define PIN_RFID_RST 22

#define PIN_IR_A 35
#define PIN_IR_B 33

// Servo MG90S — nguồn 5V riêng, tránh sụt áp
#define PIN_SERVO 14

// ── SERVO ANGLES ─────────────────────────────────────────────
#define SERVO_OPEN_ANGLE 150
#define SERVO_CLOSE_ANGLE 90

// ── DEVICE ID ────────────────────────────────────────────────
#define DEVICE_ID "parking_node"

#define HARDWARE_EVENT_PATH "/api/hardware/event"

// ── TIMING (ms) ──────────────────────────────────────────────
#define IR_WAIT_TIMEOUT_MS 8000
#define GATE_CLOSE_DELAY_MS 1000     // Barie mở cố định 1s rồi tự đóng (KHÔNG dùng IR)
#define TELEMETRY_INTERVAL_MS 15000  // Gửi telemetry mỗi 15s
#define WIFI_RETRY_INTERVAL_MS 30000 // Thử kết nối lại WiFi mỗi 30s

