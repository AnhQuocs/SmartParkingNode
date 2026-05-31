#pragma once


#include <Arduino.h>
#include <SPI.h>
#include <MFRC522.h>
#include "config.h"

class RFIDReader {
public:
    RFIDReader() : _rfid(PIN_RFID_SS, PIN_RFID_RST) {}

    void begin() {
        SPI.begin();
        _rfid.PCD_Init();
        Serial.println("[RFID] RC522 initialized");
    }

    // Trả về chuỗi UID nếu có thẻ mới, trả về "" nếu không
    String readUID() {
        if (!_rfid.PICC_IsNewCardPresent()) return "";
        if (!_rfid.PICC_ReadCardSerial())   return "";

        String uid = "";
        for (byte i = 0; i < _rfid.uid.size; i++) {
            if (_rfid.uid.uidByte[i] < 0x10) uid += "0";
            uid += String(_rfid.uid.uidByte[i], HEX);
        }
        uid.toUpperCase();

        _rfid.PICC_HaltA();
        _rfid.PCD_StopCrypto1();
        return uid;
    }

private:
    MFRC522 _rfid;
};
