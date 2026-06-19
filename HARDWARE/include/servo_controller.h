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
        // 1. Khởi tạo Servo
        _servo.attach(PIN_SERVO);
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier closed at init");

        // 2. Khởi tạo DFPlayer (Serial2)
        _dfSerial.begin(9600, SERIAL_8N1, PIN_DF_RX, PIN_DF_TX);
        delay(1000); // Chờ DFPlayer boot xong

        // Dùng (true, true) để ép thư viện chạy non-blocking, tránh treo mạch
        if (_dfPlayer.begin(_dfSerial, true, true))
        {
            _dfPlayer.setTimeOut(500);
            _dfPlayer.volume(25); // Mức âm lượng (0 - 30)
            _audioReady = true;
            Serial.println("[AUDIO] DFPlayer Mini ready");
        }
        else
        {
            _audioReady = false;
            Serial.println("[AUDIO] LỖI: DFPlayer not found — no audio");
        }
    }

    void open()
    {
        playAudio(AUDIO_GATE_OPEN_CLOSE); // Báo âm thanh cổng mở
        _servo.write(SERVO_OPEN_ANGLE);
        Serial.println("[SERVO] Barrier OPEN");
    }

    void close()
    {
        playAudio(AUDIO_GATE_OPEN_CLOSE); // Báo âm thanh cổng đóng
        _servo.write(SERVO_CLOSE_ANGLE);
        Serial.println("[SERVO] Barrier CLOSED");
    }

    void playAudio(int track)
    {
        // Nếu mạch Audio lỗi -> Bỏ qua lệnh phát để không treo ESP32
        if (!_audioReady)
            return;

        _dfPlayer.play(track);
        Serial.printf("[AUDIO] Playing track %d\n", track);
    }

private:
    Servo _servo;
    HardwareSerial _dfSerial{2};
    DFRobotDFPlayerMini _dfPlayer;
    bool _audioReady = false;
};