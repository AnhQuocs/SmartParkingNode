/**
 * speed_sensor.h / speed_sensor.cpp
 * Module đo tốc độ bằng 2 cảm biến IR + Moving Average Filter
 *
 * NGUYÊN LÝ:
 *   ┌──────────────────────────────────────────┐
 *   │  [IR1]────────── d ──────────[IR2]       │
 *   │    ↑ t1                        ↑ t2      │
 *   │                                          │
 *   │  v = d / (t2 - t1)                       │
 *   └──────────────────────────────────────────┘
 *
 * CÁC TRẠNG THÁI ĐO:
 *   IDLE        → chờ xe
 *   TRIGGERED   → IR1 đã cắt, đang chờ IR2
 *   COMPLETE    → IR2 đã cắt, sẵn sàng tính toán
 *   TIMEOUT     → IR2 không kích hoạt trong thời gian cho phép
 */

#ifndef SPEED_SENSOR_H
#define SPEED_SENSOR_H

#include <Arduino.h>
#include "config.h"

// ─────────────────────────────────────────────
//  TRẠNG THÁI MÁY TRẠNG THÁI (State Machine)
// ─────────────────────────────────────────────
enum MeasureState {
    STATE_IDLE,
    STATE_TRIGGERED,    // IR1 đã kích hoạt
    STATE_COMPLETE,     // IR2 đã kích hoạt, dữ liệu sẵn sàng
    STATE_TIMEOUT       // Không hoàn thành trong thời gian cho phép
};

// ─────────────────────────────────────────────
//  BIẾN CHIA SẺ VỚI ISR — phải là volatile
// ─────────────────────────────────────────────
volatile unsigned long g_t1_us = 0;         // Thời điểm IR1 cắt (micro-giây)
volatile unsigned long g_t2_us = 0;         // Thời điểm IR2 cắt (micro-giây)
volatile MeasureState  g_measureState = STATE_IDLE;

// ─────────────────────────────────────────────
//  ISR — PHẢI KHAI BÁO TRƯỚC KHI DÙNG
// ─────────────────────────────────────────────

/**
 * ISR cho IR1: Ghi nhận thời điểm xe vào
 * Chỉ chấp nhận nếu đang ở trạng thái IDLE
 */
void IRAM_ATTR isr_ir1_handler() {
    if (g_measureState == STATE_IDLE) {
        g_t1_us       = micros();
        g_measureState = STATE_TRIGGERED;
    }
}

/**
 * ISR cho IR2: Ghi nhận thời điểm xe ra
 * Chỉ chấp nhận nếu IR1 đã được kích hoạt
 */
void IRAM_ATTR isr_ir2_handler() {
    if (g_measureState == STATE_TRIGGERED) {
        g_t2_us       = micros();
        g_measureState = STATE_COMPLETE;
    }
}

// ─────────────────────────────────────────────
//  CẤU TRÚC KẾT QUẢ ĐO
// ─────────────────────────────────────────────
struct SpeedResult {
    bool    valid;          // true nếu đo thành công
    float   rawSpeed_kmh;   // Tốc độ thô (chưa lọc)
    float   filtSpeed_kmh;  // Tốc độ sau Moving Average
    unsigned long dt_us;    // Thời gian xe đi qua (micro-giây), để debug
    String  reason;         // Lý do invalid (nếu có)
};

// ─────────────────────────────────────────────
//  MOVING AVERAGE FILTER
// ─────────────────────────────────────────────
class MovingAverageFilter {
private:
    float    buffer[MA_WINDOW_SIZE];
    uint8_t  head  = 0;
    uint8_t  count = 0;

public:
    MovingAverageFilter() {
        memset(buffer, 0, sizeof(buffer));
    }

    /**
     * Thêm mẫu mới, trả về giá trị trung bình
     * Dùng circular buffer O(1) — không tính lại toàn bộ mỗi lần
     */
    float addSample(float sample) {
        buffer[head] = sample;
        head  = (head + 1) % MA_WINDOW_SIZE;
        if (count < MA_WINDOW_SIZE) count++;

        float sum = 0.0f;
        for (uint8_t i = 0; i < count; i++) sum += buffer[i];
        return sum / count;
    }

    void reset() {
        memset(buffer, 0, sizeof(buffer));
        head  = 0;
        count = 0;
    }

    uint8_t getSampleCount() { return count; }
};

// ─────────────────────────────────────────────
//  CLASS CHÍNH: SpeedSensor
// ─────────────────────────────────────────────
class SpeedSensor {
private:
    MovingAverageFilter  maFilter;
    unsigned long        triggerTime_ms = 0;   // Dùng để kiểm tra timeout

public:
    /**
     * Khởi tạo: cấu hình chân GPIO và gắn ngắt
     */
    void begin() {
        pinMode(PIN_IR1, INPUT_PULLUP);
        pinMode(PIN_IR2, INPUT_PULLUP);

        attachInterrupt(digitalPinToInterrupt(PIN_IR1), isr_ir1_handler, FALLING);
        attachInterrupt(digitalPinToInterrupt(PIN_IR2), isr_ir2_handler, FALLING);

        DBGLN("[SpeedSensor] Khởi tạo xong. Đang chờ xe...");
    }

    /**
     * Gọi trong loop() mỗi chu kỳ
     * Trả về true nếu có kết quả mới để xử lý
     */
    bool update(SpeedResult &result) {

        // ── Kiểm tra timeout ──
        if (g_measureState == STATE_TRIGGERED) {
            if (triggerTime_ms == 0) {
                triggerTime_ms = millis();   // Lần đầu vào STATE_TRIGGERED
            }
            if (millis() - triggerTime_ms > MEASUREMENT_TIMEOUT_MS) {
                DBGLN("[SpeedSensor] TIMEOUT — Xe không qua IR2, huỷ phiên đo.");
                g_measureState = STATE_IDLE;
                triggerTime_ms = 0;
                return false;
            }
        }

        // ── Chưa có kết quả ──
        if (g_measureState != STATE_COMPLETE) return false;

        // ── Đọc dữ liệu từ ISR (atomic read) ──
        noInterrupts();
        unsigned long t1 = g_t1_us;
        unsigned long t2 = g_t2_us;
        interrupts();

        triggerTime_ms = 0;

        // ── Tính Δt ──
        if (t2 <= t1) {
            result = { false, 0, 0, 0, "t2 <= t1: xung ngắt bị đảo" };
            resetState();
            return true;
        }

        unsigned long dt_us = t2 - t1;

        // ── Tính vận tốc thô ──
        // v = d(m) / dt(s) → km/h
        float dt_s      = (float)dt_us / 1e6f;
        float speed_ms  = SENSOR_DISTANCE_M / dt_s;
        float speed_kmh = speed_ms * 3.6f;

        DBGF("[SpeedSensor] Δt=%lu µs | d=%.2fcm | Raw=%.2f km/h\n",
             dt_us, SENSOR_DISTANCE_CM, speed_kmh);

        // ── Lọc kết quả vô lý ──
        if (speed_kmh < SPEED_MIN_KMH) {
            result = { false, speed_kmh, 0, dt_us,
                       "Tốc độ quá thấp (người đi bộ hoặc xe dừng)" };
            resetState();
            return true;
        }
        if (speed_kmh > SPEED_MAX_KMH) {
            result = { false, speed_kmh, 0, dt_us,
                       "Tốc độ quá cao (nhiễu IR hoặc vật nhỏ bay qua)" };
            resetState();
            return true;
        }

        // ── Áp dụng Moving Average ──
        float filtered = maFilter.addSample(speed_kmh);

        DBGF("[SpeedSensor] MA(%d mẫu)=%.2f km/h\n",
             maFilter.getSampleCount(), filtered);

        result = { true, speed_kmh, filtered, dt_us, "" };
        resetState();
        return true;
    }

    /**
     * Đặt lại trạng thái để đo xe tiếp theo
     */
    void resetState() {
        noInterrupts();
        g_measureState = STATE_IDLE;
        g_t1_us        = 0;
        g_t2_us        = 0;
        interrupts();
        triggerTime_ms = 0;
    }

    /**
     * Dành cho hiệu chỉnh thực tế: đo thời gian tối thiểu hợp lệ
     * Xe chạy 120 km/h qua d=30cm → Δt = 0.3/33.33 = 9ms
     * Xe chạy 1 km/h qua d=30cm  → Δt = 0.3/0.278 = 1080ms
     */
    void printCalibrationInfo() {
        float t_fast_ms = (SENSOR_DISTANCE_M / (SPEED_MAX_KMH / 3.6f)) * 1000.0f;
        float t_slow_ms = (SENSOR_DISTANCE_M / (SPEED_MIN_KMH / 3.6f)) * 1000.0f;
        DBGF("[Calibration] d=%.1fcm | Δt hợp lệ: %.1f ms ~ %.0f ms\n",
             SENSOR_DISTANCE_CM, t_fast_ms, t_slow_ms);
    }
};

#endif // SPEED_SENSOR_H
