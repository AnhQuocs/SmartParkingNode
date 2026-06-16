
#include <Arduino.h>
#include <WiFi.h>

#include "config.h"
#include "secrets.h"
#include "rfid_reader.h"
#include "speed_sensor.h"
#include "servo_controller.h"
#include "firebase_manager.h"

ServoController barrier;
FirebaseManager firebase;
RFIDReader      rfid;
SpeedSensor     irSensor;

enum GateState { IDLE, WAITING_IR, CONFIRMED };
GateState     gateState     = IDLE;
String        pendingUID    = "";
String        pendingAction = ""; // "IN" hoặc "OUT"
unsigned long stateTs       = 0;  // mốc thời gian cho timeout tổng (mở Barie)

unsigned long lastTelemetryMs = 0;
unsigned long lastWifiRetryMs = 0;

// ── WiFi ─────────────────────────────────────────────────────
void connectWiFi(const String &ssid, const String &pass) {
    Serial.printf("[WIFI] Connecting to %s ...\n", ssid.c_str());
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid.c_str(), pass.c_str());
    unsigned long t = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - t < 15000) {
        delay(500); // chỉ chạy lúc setup/reconnect, không nằm trong loop chính
        Serial.print(".");
    }
    Serial.println();
    if (WiFi.status() == WL_CONNECTED)
        Serial.printf("[WIFI] Connected - IP: %s\n", WiFi.localIP().toString().c_str());
    else
        Serial.println("[WIFI] Failed");
}

// ── Mở Barie và bắt đầu theo dõi IR — DÙNG CHUNG cho mọi nguồn lệnh ──
void openGateAndArm(const String &uid, const String &action) {
    pendingUID    = uid;
    pendingAction = action;

    // 1. PHẦN CỨNG TRƯỚC
    barrier.open();
    irSensor.arm();          
    gateState = WAITING_IR;
    stateTs   = millis();

    // 2. Firebase SAU — nếu lỗi/chậm cũng không ảnh hưởng Barie
    firebase.setGateStatus("open");

    Serial.printf("[GATE] Mở Barie cho UID: %s Action: %s\n", uid.c_str(), action.c_str());
}

// ── Lệnh từ Cloud/App (qua polling cloud_command) ────────────
void onCloudCommand(String cmd, String uid, String action) {
    if (cmd == "OPEN") {
        openGateAndArm(uid, action.length() ? action : "IN");

    } else if (cmd == "CLOSE") {
        barrier.close();
        gateState = IDLE;
        irSensor.reset();
        firebase.setGateStatus("auto");

    } else if (cmd == "BUZZER_ALERT") {
        barrier.playAudio(AUDIO_DEBT_EXCEED);

    } else if (cmd == "CARD_UNKNOWN") {
        barrier.playAudio(AUDIO_CARD_UNKNOWN);
    }
}

// ── Xác nhận xe đã đi qua hoàn toàn (IR báo true) ────────────
void commitTransaction() {
    // 1. PHẦN CỨNG TRƯỚC — luôn chạy dù Firebase có lỗi
    if (pendingAction == "IN")
        barrier.playAudio(AUDIO_WELCOME);
    else
        barrier.playAudio(AUDIO_GOODBYE);

    barrier.close();              // đóng ngay khi xác nhận xe đã qua
    gateState = IDLE;             // về IDLE ngay — sẵn sàng nhận thẻ tiếp theo

    Serial.printf("[COMMIT] %s - UID: %s\n", pendingAction.c_str(), pendingUID.c_str());

    // 2. Firebase SAU
    firebase.confirmIR();
    firebase.setGateStatus("auto");

    pendingUID    = "";
    pendingAction = "";
}

// ── Hết thời gian chờ xe vào cổng (chưa từng chắn IR_A) ──────
void revertTransaction() {
    // PHẦN CỨNG TRƯỚC
    barrier.close();
    gateState = IDLE;
    irSensor.reset();
    Serial.println("[REVERT] Không phát hiện xe — hủy giao dịch, không ghi log.");

    // Firebase SAU
    firebase.revertSwipe();
    firebase.setGateStatus("auto");

    pendingUID    = "";
    pendingAction = "";
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
    Serial.println("\n[BOOT] Smart Parking Node starting...");

    rfid.begin();
    irSensor.begin();
    barrier.begin();

    connectWiFi(WIFI_SSID, WIFI_PASSWORD);

    if (WiFi.status() == WL_CONNECTED) {
        firebase.begin();
        firebase.scanAndUploadNetworks();
    }

    Serial.println("[BOOT] Setup complete.");
}

// ═══════════════════════════════════════════════════════════════
void loop() {

    // ── 1. Lệnh từ Cloud (polling, không block lâu) ──────────
    firebase.loop();

    // ── 2. RFID — chỉ đọc khi đang IDLE ───────────────────────
    if (gateState == IDLE) {
        String uid = rfid.readUID();
        if (!uid.isEmpty()) {
            Serial.printf("[RFID] Card: %s\n", uid.c_str());
            // Hàm này tự gọi onCloudCommand("OPEN"/"BUZZER_ALERT"/"CARD_UNKNOWN")
            firebase.processSwipeOnHardware(uid);
        }
    }

    // ── 3. Theo dõi IR khi đang chờ xe qua cổng ──────────────
    if (gateState == WAITING_IR) {

        if (irSensor.update()) {
            // update() đã TỰ xác nhận "xe qua hoàn toàn" và tự reset nội bộ.
            // Không cần gọi irSensor.reset() lần nữa ở đây.
            commitTransaction();
        }
        else if (millis() - stateTs > TIMEOUT_REVERT_MS) {
            // Timeout tổng: từ lúc mở Barie, xe không bắt đầu đi qua
            Serial.println("[TIMEOUT] Quá thời gian chờ xe — reverting...");
            revertTransaction();
        }
    }

    // ── 4. Telemetry + check WiFi đổi từ xa ──────────────────
    if (millis() - lastTelemetryMs > TELEMETRY_INTERVAL_MS) {
        lastTelemetryMs = millis();
        bool irA_ok = (digitalRead(PIN_IR_A) != -1);
        bool irB_ok = (digitalRead(PIN_IR_B) != -1);
        firebase.sendTelemetry(irA_ok, irB_ok);
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

    delay(20); // chỉ để nhường CPU, không ảnh hưởng phản hồi thực tế
}
