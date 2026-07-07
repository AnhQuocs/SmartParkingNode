#pragma once

// ============================================================
//  config.h — Cấu hình chân GPIO và hằng số hệ thống
//  Đồng bộ với Long (Hardware Engineer)
// ============================================================

// ── PIN MAP ──────────────────────────────────────────────────
// RFID RC522 (SPI)
#define PIN_RFID_SS 5
#define PIN_RFID_RST 22
// SCK=18, MOSI=23, MISO=19 — SPI mặc định của ESP32

// IR Sensors — GPIO 34,35 là INPUT_ONLY, KHÔNG có internal pull-up
// PIN_IR_A = cảm biến NGOÀI cổng (xe chạm đầu tiên khi đi VÀO)
// PIN_IR_B = cảm biến TRONG cổng (xe chạm sau cùng khi đi VÀO)
#define PIN_IR_A 35
#define PIN_IR_B 33

// Servo MG90S — nguồn 5V riêng, tránh sụt áp
#define PIN_SERVO 14

// DFPlayer Mini — dùng Serial2 (RX2=16, TX2=17)
#define PIN_DF_RX 16
#define PIN_DF_TX 17

// ── SERVO ANGLES ─────────────────────────────────────────────
#define SERVO_OPEN_ANGLE 180
#define SERVO_CLOSE_ANGLE 90

// ── DEVICE ID ────────────────────────────────────────────────
#define DEVICE_ID "parking_node_01"

#define BACKEND_HOST "7e58-42-118-50-38.ngrok-free.app"
#define HARDWARE_EVENT_PATH "/api/hardware/event"

// ── TIMING (ms) ──────────────────────────────────────────────
#define IR_WAIT_TIMEOUT_MS 8000
#define GATE_CLOSE_DELAY_MS 1000     // Barie mở cố định 1s rồi tự đóng (KHÔNG dùng IR)
#define TELEMETRY_INTERVAL_MS 15000  // Gửi telemetry mỗi 15s
#define WIFI_RETRY_INTERVAL_MS 30000 // Thử kết nối lại WiFi mỗi 30s

// ── AUDIO TRACKS ─────────────────────────────────────────────
// File đặt trong SD card DFPlayer: 001.mp3, 002.mp3, ...
#define AUDIO_GATE_OPEN_CLOSE 1 // "Cổng đang mở/đóng"
#define AUDIO_DEBT_EXCEED 2     // "Vượt hạn mức công nợ"
#define AUDIO_CARD_UNKNOWN 3    // "Thẻ chưa đăng ký"
