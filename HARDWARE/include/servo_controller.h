#pragma once

#include <Arduino.h>
#include <ESP32Servo.h>
#include <HardwareSerial.h>
#include <DFRobotDFPlayerMini.h>
#include "config.h"

class ServoController {
public:
    void begin() {
        // Servo
        _servo.attach(PIN_SERVO);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier closed at init");

        // DFPlayer — Serial2
        _dfSerial.begin(9600, SERIAL_8N1, PIN_DF_RX, PIN_DF_TX);
        delay(500);
        if (_dfPlayer.begin(_dfSerial)) {
            _dfPlayer.volume(25);   // 0–30
            Serial.println("[AUDIO] DFPlayer Mini ready");
        } else {
            Serial.println("[AUDIO] DFPlayer not found — no audio");
        }
    }

    void open() {
        _servo.write(SERVO_OPEN_ANGLE);
        Serial.println("[SERVO] Barrier OPEN");
    }

    void close() {
        playAudio(AUDIO_GATE_CLOSING);
        delay(800);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier CLOSED");
    }

    void playAudio(int track) {
        _dfPlayer.play(track);
        Serial.printf("[AUDIO] Playing track %d\n", track);
    }

private:
    Servo               _servo;
    HardwareSerial      _dfSerial{2};
    DFRobotDFPlayerMini _dfPlayer;
};
