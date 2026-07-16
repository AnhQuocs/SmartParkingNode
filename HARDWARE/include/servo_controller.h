#pragma once

#include <Arduino.h>
#include <ESP32Servo.h>
#include "config.h"

class ServoController
{
public:
    void begin()
    {
        // Khởi tạo Servo
        _servo.attach(PIN_SERVO);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier closed at init");
    }

    void open()
    {
        _servo.write(SERVO_OPEN_ANGLE);
        Serial.println("[SERVO] Barrier OPEN");
    }

    void close()
    {
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier CLOSED");
    }

private:
    Servo _servo;
};