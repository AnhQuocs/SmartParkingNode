// ============================================================
//  main.cpp — Smart Parking Node (PHIÊN BẢN ĐƠN GIẢN — KHÔNG IR)
//
//  Luồng:
//    Quẹt thẻ → check Firestore (isActive)
//      → true  : mở Barie, ghi gate_control=open, ghi log
//      → false : phát cảnh báo công nợ
//      → unknown: phát cảnh báo + đẩy pending_cards
//    Sau GATE_OPEN_DURATION_MS (cố định) → tự đóng Barie
//
//  Không dùng cảm biến IR — không có khái niệm "xe đi qua".
//  Việc xác định hướng IN/OUT có thể đơn giản hoá theo trạng
//  thái nợ hoặc do App quyết định (xem TODO dưới).
// ============================================================

#include <Arduino.h>
#include <WiFi.h>

#include "config.h"
#include "secrets.h"
#include "rfid_reader.h"
#include "servo_controller.h"
#include "firebase_manager.h"

ServoController barrier;
FirebaseManager firebase;
RFIDReader      rfid;

enum GateState { IDLE, GATE_OPEN };
GateState     gateState  = IDLE;
String        pendingUID = "";
unsigned long openedAtMs = 0;

unsigned long lastTelemetryMs = 0;
unsigned long lastWifiRetryMs = 0;

// ── WiFi ─────────────────────────────────────────────────────
void connectWiFi(const String &ssid, const String &pass) {
    Serial.printf("[WIFI] Connecting to %s ...\n", ssid.c_str());
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid.c_str(), pass.c_str());
    unsigned long t = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - t < 15000) {
        delay(500);
        Serial.print(".");
    }
    Serial.println();
    if (WiFi.status() == WL_CONNECTED)
        Serial.printf("[WIFI] Connected - IP: %s\n", WiFi.localIP().toString().c_str());
    else
        Serial.println("[WIFI] Failed");
}

// ── Mở Barie ─────────────────────────────────────────────────
void openGate(const String &uid) {
    pendingUID = uid;

    // PHẦN CỨNG TRƯỚC
    barrier.open();
    gateState  = GATE_OPEN;
    openedAtMs = millis();

    // Firebase SAU — lỗi mạng không ảnh hưởng Barie
    firebase.setGateStatus("open");

    Serial.printf("[GATE] Mở Barie cho UID: %s — sẽ tự đóng sau %d ms\n",
                  uid.c_str(), GATE_OPEN_DURATION_MS);
}

// ── Đóng Barie (do hết giờ hoặc lệnh CLOSE) ──────────────────
void closeGate() {
    // PHẦN CỨNG TRƯỚC
    barrier.playAudio(AUDIO_GOODBYE);
    barrier.close();
    gateState = IDLE;

    Serial.printf("[GATE] Đóng Barie — UID vừa xử lý: %s\n", pendingUID.c_str());

    // Firebase SAU
    firebase.confirmIR();           // dùng lại field này để báo "đã xử lý xong"
    firebase.setGateStatus("auto");

    pendingUID = "";
}

// ── Lệnh từ Cloud/App ────────────────────────────────────────
void onCloudCommand(String cmd, String uid, String action) {
    if (cmd == "OPEN") {
        openGate(uid);

    } else if (cmd == "CLOSE") {
        if (gateState == GATE_OPEN) closeGate();

    } else if (cmd == "BUZZER_ALERT") {
        barrier.playAudio(AUDIO_DEBT_EXCEED);

    } else if (cmd == "CARD_UNKNOWN") {
        barrier.playAudio(AUDIO_CARD_UNKNOWN);
    }
}

// ── Đổi WiFi từ xa ───────────────────────────────────────────
void handleRemoteWiFiChange() {
    String newSSID, newPass;
    if (!firebase.checkRemoteWiFiChange(newSSID, newPass)) return;

    Serial.printf("[WIFI] Remote switch -> %s\n", newSSID.c_str());
    WiFi.disconnect();
    delay(1000);
    connectWiFi(newSSID, newPass);

    bool ok = (WiFi.status() == WL_CONNECTED);
    firebase.reportWiFiSwitchResult(ok);
    if (!ok) connectWiFi(WIFI_SSID, WIFI_PASSWORD);
}

// ═══════════════════════════════════════════════════════════════
void setup() {
    Serial.begin(115200);
    Serial.println("\n[BOOT] Smart Parking Node (NO-IR mode) starting...");

    rfid.begin();
    barrier.begin();

    connectWiFi(WIFI_SSID, WIFI_PASSWORD);

    if (WiFi.status() == WL_CONNECTED) {
        firebase.begin();
        firebase.scanAndUploadNetworks();
    }

    Serial.println("[BOOT] Setup complete. Quẹt thẻ để test.");
}

// ═══════════════════════════════════════════════════════════════
void loop() {

    // ── 1. Lệnh từ Cloud (polling) ───────────────────────────
    firebase.loop();

    // ── 2. RFID — chỉ đọc khi Barie đang đóng ─────────────────
    if (gateState == IDLE) {
        String uid = rfid.readUID();
        if (!uid.isEmpty()) {
            Serial.printf("[RFID] Card: %s\n", uid.c_str());
            // Hàm này tự gọi onCloudCommand("OPEN"/"BUZZER_ALERT"/"CARD_UNKNOWN")
            firebase.processSwipeOnHardware(uid);
        }
    }

    // ── 3. Tự đóng Barie sau thời gian cố định ───────────────
    if (gateState == GATE_OPEN) {
        if (millis() - openedAtMs > GATE_OPEN_DURATION_MS) {
            closeGate();
        }
    }

    // ── 4. Telemetry + check WiFi đổi từ xa ──────────────────
    if (millis() - lastTelemetryMs > TELEMETRY_INTERVAL_MS) {
        lastTelemetryMs = millis();
        // Không còn IR nên báo cứng "N/A" cho 2 trường này
        firebase.sendTelemetry(false, false);
        handleRemoteWiFiChange();
    }

    // ── 5. WiFi watchdog ──────────────────────────────────────
    if (WiFi.status() != WL_CONNECTED) {
        if (millis() - lastWifiRetryMs > WIFI_RETRY_INTERVAL_MS) {
            lastWifiRetryMs = millis();
            Serial.println("[WIFI] Lost - retrying...");
            connectWiFi(WIFI_SSID, WIFI_PASSWORD);
            if (WiFi.status() == WL_CONNECTED) {
                firebase.begin();
                firebase.scanAndUploadNetworks();
            }
        }
    }

    delay(20);
}
