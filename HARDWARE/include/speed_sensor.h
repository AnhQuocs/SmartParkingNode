#pragma once

#include <Arduino.h>
#include "config.h"

class SpeedSensor
{
public:
    enum Direction
    {
        NONE,
        IN,
        OUT
    };

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
        _direction = NONE;
    }

    bool isDetecting()
    {
        return (digitalRead(PIN_IR_A) == LOW) || (digitalRead(PIN_IR_B) == LOW);
    }

    Direction getDirection()
    {
        return _direction;
    }

    // Gọi trong loop() — trả về true khi xe đã đi qua hoàn toàn
    bool update()
    {
        bool irA = (digitalRead(PIN_IR_A) == LOW);
        bool irB = (digitalRead(PIN_IR_B) == LOW);

        // Phát hiện cảm biến nào bị che ĐẦU TIÊN để xác định chiều đi
        if (irA && !_irA_triggered && !_irB_triggered)
        {
            _direction = IN;
            _irA_triggered = true;
            Serial.println("[IR] Xe đi VÀO (Che IR_A trước)");
        }
        else if (irB && !_irB_triggered && !_irA_triggered)
        {
            _direction = OUT;
            _irB_triggered = true;
            Serial.println("[IR] Xe đi RA (Che IR_B trước)");
        }

        // Chờ xe lọt qua cảm biến còn lại
        if (_direction == IN && irB && !_irB_triggered)
        {
            _irB_triggered = true;
            Serial.println("[IR] Đã che IR_B (Đang lọt qua cổng VÀO)");
        }
        else if (_direction == OUT && irA && !_irA_triggered)
        {
            _irA_triggered = true;
            Serial.println("[IR] Đã che IR_A (Đang lọt qua cổng RA)");
        }

        // Xác nhận khi cả 2 đã bị che và hiện tại đã quang đãng
        if (_irA_triggered && _irB_triggered && !irA && !irB)
        {
            Serial.println("[IR] Xe đã đi qua hoàn toàn!");
            reset();
            return true; 
        }

        return false;
    }

private:
    bool _irA_triggered = false;
    bool _irB_triggered = false;
    Direction _direction = NONE;
};