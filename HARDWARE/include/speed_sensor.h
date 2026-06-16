#pragma once

#include <Arduino.h>
#include "config.h"

class SpeedSensor
{
public:
    enum Status
    {
        IDLE_STATE,  // Chưa quẹt thẻ / đã xử lý xong
        ARMED,       // Đang chờ xe chắn IR_A
        A_TRIGGERED, // IR_A đã chắn, đang chờ IR_B
        B_TRIGGERED  // IR_B đã chắn, đang chờ cả 2 thả ra để xác nhận
    };

    void begin()
    {
        pinMode(PIN_IR_A, INPUT);
        pinMode(PIN_IR_B, INPUT);
        _status = IDLE_STATE;
        Serial.println("[IR] Sensors initialized");
    }

    // Gọi NGAY sau khi mở Barie — bắt đầu theo dõi 1 lượt xe mới, state sạch 100%
    void arm()
    {
        _status = ARMED;
        _lastEventMs = millis();
        Serial.println("[IR] Armed — chờ xe chắn IR_A");
    }

    // Hủy theo dõi (dùng khi revert do timeout tổng)
    void reset()
    {
        _status = IDLE_STATE;
    }

    bool isArmed() { return _status != IDLE_STATE; }
    bool isDetecting() { return readA() || readB(); }

    // Gọi liên tục trong loop(). Trả về true CHÍNH XÁC 1 LẦN khi xe đã qua hoàn toàn.
    // Tự reset nội bộ về IDLE_STATE ngay khi trả true — không cần gọi reset() ngoài.
    bool update()
    {
        if (_status == IDLE_STATE)
            return false;

        bool a = readA();
        bool b = readB();

        if (_status == ARMED)
        {
            if (a)
            {
                _status = A_TRIGGERED;
                _lastEventMs = millis();
                Serial.println("[IR] IR_A chắn — xe đang vào cổng");
            }
            return false;
        }

        if (_status == A_TRIGGERED)
        {
            if (b)
            {
                _status = B_TRIGGERED;
                _lastEventMs = millis();
                Serial.println("[IR] IR_B chắn — xe đang lọt qua");
                return false;
            }
            if (millis() - _lastEventMs > IR_SEQUENCE_TIMEOUT_MS)
            {
                Serial.println("[IR] Timeout chờ IR_B — hủy theo dõi lượt này");
                _status = IDLE_STATE;
            }
            return false;
        }

        if (_status == B_TRIGGERED)
        {
            if (!a && !b)
            {
                Serial.println("[IR] Xe đã đi qua hoàn toàn!");
                _status = IDLE_STATE;
                return true;
            }
            if (millis() - _lastEventMs > IR_SEQUENCE_TIMEOUT_MS)
            {
                Serial.println("[IR] Cảnh báo: vẫn còn vật sau timeout — vẫn xác nhận qua");
                _status = IDLE_STATE;
                return true;
            }
            return false;
        }

        return false;
    }

private:
    Status _status = IDLE_STATE;
    unsigned long _lastEventMs = 0;

    bool readA() { return digitalRead(PIN_IR_A) == LOW; } // LOW = có vật
    bool readB() { return digitalRead(PIN_IR_B) == LOW; }
};
