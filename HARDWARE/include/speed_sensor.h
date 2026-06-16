#pragma once

#include <Arduino.h>
#include "config.h"

class SpeedSensor
{
public:
    void begin()
    {
        pinMode(PIN_IR_A, INPUT);
        pinMode(PIN_IR_B, INPUT);
        reset();
        Serial.println("[IR] Sensors initialized on GPIO 34, 35");
    }

    void reset()
    {
        _irA_triggered = false;
        _irB_triggered = false;
    }

    // HÀM MỚI: Kiểm tra xem có xe đang đứng chắn ở cổng không (Tính năng an toàn)
    bool isDetecting()
    {
        return (digitalRead(PIN_IR_A) == LOW) || (digitalRead(PIN_IR_B) == LOW);
    }

    // Gọi trong loop() — trả về true khi xe đã đi qua hoàn toàn
    bool update()
    {
        bool irA = (digitalRead(PIN_IR_A) == LOW); // LOW = có vật
        bool irB = (digitalRead(PIN_IR_B) == LOW);

        if (irA && !_irA_triggered)
        {
            _irA_triggered = true;
            Serial.println("[IR] IR_A triggered");
        }
        if (_irA_triggered && irB && !_irB_triggered)
        {
            _irB_triggered = true;
            Serial.println("[IR] IR_B triggered — vehicle passing");
        }

        if (_irA_triggered && _irB_triggered && !irB)
        {
            Serial.println("[IR] Vehicle passed completely");
            reset();
            return true;
        }
        return false;
    }

private:
    bool _irA_triggered = false;
    bool _irB_triggered = false;
};