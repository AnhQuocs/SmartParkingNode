#pragma once

// ============================================================
//  speed_sensor.h — PHIÊN BẢN ĐƠN GIẢN
//
//  Yêu cầu: Xe đi VÀO → chỉ cần chờ IR_B (cảm biến phía trong)
//                        chắn rồi thả ra → xe đã qua.
//           Xe đi RA  → chỉ cần chờ IR_A (cảm biến phía ngoài)
//                        chắn rồi thả ra → xe đã qua.
//  Không theo dõi chuỗi 2 cảm biến, không có state phức tạp.
//  Chỉ 1 cảm biến "đích" cần theo dõi tùy theo hướng đã biết
//  trước (do hệ thống xác định IN/OUT lúc quẹt thẻ).
// ============================================================

#include <Arduino.h>
#include "config.h"

class SpeedSensor
{
public:
    enum Direction
    {
        DIR_IN,
        DIR_OUT
    };
    enum Result
    {
        NONE,
        CONFIRMED,
        TIMED_OUT
    };

    void begin()
    {
        pinMode(PIN_IR_A, INPUT);
        pinMode(PIN_IR_B, INPUT);
        _watching = false;
        Serial.println("[IR] Sensors initialized");
    }

    // Gọi NGAY khi mở Barie, truyền hướng xe đã biết (IN hoặc OUT)
    void startWatch(Direction dir)
    {
        _direction = dir;
        _watching = true;
        _targetTriggered = false;
        _startMs = millis();

        Serial.printf("[IR] Theo dõi cảm biến %s — hướng %s\n",
                      (dir == DIR_IN) ? "IR_B" : "IR_A",
                      (dir == DIR_IN) ? "VÀO" : "RA");
    }

    void stopWatch()
    {
        _watching = false;
    }

    bool isWatching() { return _watching; }

    Result update()
    {
        if (!_watching)
            return NONE;

        bool targetBlocked = readTarget();

        // DEBUG: in giá trị thô 2 chân mỗi 500ms để xác định cảm biến nào đang chắn thật
        if (millis() - _lastDebugMs > 500)
        {
            _lastDebugMs = millis();
            Serial.printf("[IR-DEBUG] IR_A(raw)=%d IR_B(raw)=%d | đang theo dõi: %s | target=%s\n",
                          digitalRead(PIN_IR_A), digitalRead(PIN_IR_B),
                          (_direction == DIR_IN) ? "IR_B" : "IR_A",
                          targetBlocked ? "CHẮN" : "trống");
        }

        if (targetBlocked && !_targetTriggered)
        {
            _targetTriggered = true;
            Serial.println("[IR] Cảm biến đích đã chắn — xe đang đi qua");
        }

        // Đã chắn rồi giờ thả ra → xe đã đi qua hoàn toàn — XÁC NHẬN THẬT
        if (_targetTriggered && !targetBlocked)
        {
            Serial.println("[IR] Xe đã đi qua cảm biến đích — XÁC NHẬN THẬT");
            _watching = false;
            return CONFIRMED;
        }

        // An toàn: quá lâu không thấy gì hoặc xe đứng kẹt mãi trên cảm biến
        // → đóng Barie cho an toàn nhưng KHÔNG coi là giao dịch hợp lệ
        if (millis() - _startMs > IR_WAIT_TIMEOUT_MS)
        {
            Serial.println("[IR] TIMEOUT — không xác nhận được xe đi qua. KHÔNG ghi Firestore.");
            _watching = false;
            return TIMED_OUT;
        }

        return NONE;
    }

private:
    bool _watching = false;
    bool _targetTriggered = false;
    Direction _direction = DIR_IN;
    unsigned long _startMs = 0;
    unsigned long _lastDebugMs = 0;

    // Cảm biến "đích" tùy theo hướng: IN theo dõi IR_B, OUT theo dõi IR_A
    bool readTarget()
    {
        if (_direction == DIR_IN)
            return digitalRead(PIN_IR_B) == LOW; // LOW = có vật
        else
            return digitalRead(PIN_IR_A) == LOW;
    }
};