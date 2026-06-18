#pragma once

#include <Arduino.h>
#include <ESP32Servo.h>
#include <HardwareSerial.h>
#include <DFRobotDFPlayerMini.h>
#include "config.h"

class ServoController
{
public:
    void begin()
    {
        _servo.attach(PIN_SERVO);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier closed at init");

        _dfSerial.begin(9600, SERIAL_8N1, PIN_DF_RX, PIN_DF_TX);
        delay(300); // chỉ ở begin(), chạy 1 lần lúc boot — không ảnh hưởng loop
        if (_dfPlayer.begin(_dfSerial))
        {
            _dfPlayer.volume(25);
            Serial.println("[AUDIO] DFPlayer Mini ready");
        }
        else
        {
            Serial.println("[AUDIO] DFPlayer not found — no audio");
        }
    }

    void open()
    {
        _servo.write(SERVO_OPEN_ANGLE);
        Serial.println("[SERVO] Barrier OPEN");
    }

    // Đóng ngay, không delay. Âm thanh cảnh báo phát đồng thời (không chờ).
    void close()
    {
        playAudio(AUDIO_GATE_CLOSING);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier CLOSED");
    }

    void playAudio(int track)
    {
        _dfPlayer.play(track);
        Serial.printf("[AUDIO] Playing track %d\n", track);
    }

private:
    Servo _servo;
    HardwareSerial _dfSerial{2};
    DFRobotDFPlayerMini _dfPlayer;
};
