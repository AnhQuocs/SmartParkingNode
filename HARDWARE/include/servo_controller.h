/**
 * servo_controller.h — Điều khiển Servo MG90S
 *
 * ĐẤU NỐI:
 *   Dây NÂU/ĐEN  → GND
 *   Dây ĐỎ       → 5V (chân VIN/5V trên ESP32)
 *   Dây CAM/VÀNG → GPIO 13  (PIN_SERVO trong config.h)
 *
 *   ⚡ Gắn tụ 100µF giữa 5V và GND gần servo để chống sụt áp
 *
 * NGUYÊN LÝ PWM ESP32:
 *   ESP32 không dùng thư viện Servo của Arduino.
 *   Dùng LEDC (LED Control) tích hợp để tạo PWM 50Hz.
 *   Duty cycle:
 *     0°   → ~0.5ms pulse → duty = 1638  (tính theo 16-bit, 50Hz)
 *     90°  → ~1.5ms pulse → duty = 4915
 *     180° → ~2.5ms pulse → duty = 8192
 *
 * THƯ VIỆN: Không cần cài thêm, dùng ESP32 LEDC built-in.
 *
 * CÁCH DÙNG trong main.cpp:
 *   ServoController servo;
 *   servo.begin();
 *   servo.pointAt(speed_kmh);   // Tự tính góc theo tốc độ
 *   servo.sweep();              // Quét lại báo lỗi
 *   servo.center();             // Về giữa
 */

#ifndef SERVO_CONTROLLER_H
#define SERVO_CONTROLLER_H

#include <Arduino.h>
#include "config.h"

// ─────────────────────────────────────────────
//  THÔNG SỐ SERVO MG90S
// ─────────────────────────────────────────────
// PWM: 50Hz, resolution 16-bit (0–65535)
// Pulse: 0.5ms (min) → 2.5ms (max)
// 1 tick = 1/50Hz / 65536 = ~0.305µs
// 0.5ms  / 0.305µs ≈ 1638
// 2.5ms  / 0.305µs ≈ 8192

#define SERVO_FREQ       50       // Hz
#define SERVO_RES_BITS   16       // 16-bit resolution
#define SERVO_MIN_DUTY   1638     // 0°   ← 0.5ms pulse
#define SERVO_MAX_DUTY   8192     // 180° ← 2.5ms pulse
#define SERVO_LEDC_CH    0        // LEDC channel (0–15)

class ServoController {
private:
    bool _ready = false;

    /**
     * Chuyển góc (0°–180°) → duty cycle 16-bit
     */
    uint32_t angleToDuty(float angle) {
        angle = constrain(angle, 0.0f, 180.0f);
        return (uint32_t)map(
            (long)(angle * 100),
            0, 18000,
            SERVO_MIN_DUTY, SERVO_MAX_DUTY
        );
    }

    /**
     * Ghi duty cycle trực tiếp (nội bộ)
     */
    void writeDuty(uint32_t duty) {
        ledcWrite(SERVO_LEDC_CH, duty);
    }

public:
    /**
     * Khởi tạo LEDC channel và gắn vào PIN_SERVO
     * Gọi trong setup()
     */
    void begin() {
        ledcSetup(SERVO_LEDC_CH, SERVO_FREQ, SERVO_RES_BITS);
        ledcAttachPin(PIN_SERVO, SERVO_LEDC_CH);
        center();   // Về giữa (90°) khi khởi động
        _ready = true;
        DBGF("[Servo] Khởi tạo xong — GPIO%d | 50Hz | 16-bit PWM\n", PIN_SERVO);
    }

    /**
     * Xoay servo đến góc chỉ định (0°–180°)
     */
    void setAngle(float angle) {
        if (!_ready) return;
        writeDuty(angleToDuty(angle));
        DBGF("[Servo] Góc → %.1f°\n", angle);
    }

    /**
     * Về vị trí giữa (90°) — trạng thái chờ
     */
    void center() {
        setAngle(90.0f);
    }

    /**
     * Trỏ laser vào xe vi phạm dựa theo tốc độ
     *
     * Ánh xạ tốc độ → góc:
     *   Tốc độ bằng Vmax        → 90°  (trung tâm)
     *   Tốc độ = Vmax + 10 km/h → ~135° (lệch phải)
     *   Tốc độ = Vmax + 30 km/h → 180° (cực đại)
     *
     * Điều chỉnh ánh xạ này theo bố cục thực tế của trạm.
     */
    void pointAt(float speed_kmh, float vmax_kmh) {
        if (!_ready) return;

        float excess = speed_kmh - vmax_kmh;        // km/h vượt quá
        excess = constrain(excess, 0.0f, 30.0f);    // clamp 0–30 km/h

        // Ánh xạ tuyến tính: 0 km/h vượt → 90°, 30 km/h vượt → 180°
        float angle = map((long)(excess * 10),
                          0, 300,
                          90, 180);

        setAngle(angle);
        DBGF("[Servo] Vi phạm %.1f km/h (vượt %.1f) → %.1f°\n",
             speed_kmh, excess, angle);
    }

    /**
     * Quét 0° → 180° → 90° — dùng khi cảnh báo vi phạm
     * blockMs: thời gian dừng tại mỗi đầu (ms)
     */
    void sweep(uint16_t blockMs = 400) {
        if (!_ready) return;
        DBGLN("[Servo] Sweep cảnh báo...");
        setAngle(0.0f);   delay(blockMs);
        setAngle(180.0f); delay(blockMs);
        setAngle(0.0f);   delay(blockMs);
        setAngle(180.0f); delay(blockMs);
        center();
    }

    /**
     * Quét mượt từ góc hiện tại đến góc đích
     * Dùng khi muốn animation trơn, không giật
     */
    void smoothTo(float targetAngle, uint16_t durationMs = 500) {
        if (!_ready) return;
        // Đọc duty hiện tại rồi nội suy
        uint32_t startDuty = ledcRead(SERVO_LEDC_CH);
        uint32_t endDuty   = angleToDuty(targetAngle);
        uint16_t steps     = durationMs / 10;   // Mỗi bước 10ms

        for (uint16_t i = 0; i <= steps; i++) {
            uint32_t d = startDuty + (long)(endDuty - startDuty) * i / steps;
            writeDuty(d);
            delay(10);
        }
        DBGF("[Servo] Smooth → %.1f°\n", targetAngle);
    }

    /**
     * Tắt PWM (servo không giữ lực) — tiết kiệm điện khi chờ
     * Gọi sau khi servo đã về vị trí nghỉ
     */
    void detach() {
        ledcDetachPin(PIN_SERVO);
        _ready = false;
        DBGLN("[Servo] Detached — tiết kiệm điện");
    }

    bool isReady() { return _ready; }
};

#endif // SERVO_CONTROLLER_H
