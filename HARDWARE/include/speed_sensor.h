#pragma once



#include <Arduino.h>
#include "config.h"

class SpeedSensor {
public:
    void begin() {
        pinMode(PIN_IR_A, INPUT);
        pinMode(PIN_IR_B, INPUT);
        reset();
        Serial.println("[IR] Sensors initialized on GPIO 34, 35");
    }

    void reset() {
        _irA_triggered = false;
        _irB_triggered = false;
    }

    // Gọi trong loop() — trả về true khi xe đã đi qua hoàn toàn
    bool update() {
        bool irA = (digitalRead(PIN_IR_A) == HIGH);  // HIGH = có vật
        bool irB = (digitalRead(PIN_IR_B) == HIGH);

        if (irA && !_irA_triggered) {
            _irA_triggered = true;
            Serial.println("[IR] IR_A triggered");
        }
        if (_irA_triggered && irB && !_irB_triggered) {
            _irB_triggered = true;
            Serial.println("[IR] IR_B triggered — vehicle passing");
        }
        // Xe qua hoàn toàn: cả 2 đã kích và hiện không còn chắn
        if (_irA_triggered && _irB_triggered && !irA && !irB) {
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
