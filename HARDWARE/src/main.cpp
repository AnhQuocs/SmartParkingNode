// ============================================================
//  main.cpp — Smart Parking Node
//
//  Luồng:
//    Quẹt thẻ → check Firestore → biết hướng IN/OUT
//      → mở Barie → startWatch(hướng tương ứng)
//      → IN  : chỉ chờ IR_B chắn rồi thả ra
//      → OUT : chỉ chờ IR_A chắn rồi thả ra
//    → Sau khi xác nhận, đóng Barie sau GATE_CLOSE_DELAY_MS (0.5s)
//
//  Không có chuỗi A→B phức tạp. Chỉ theo dõi đúng 1 cảm biến
//  "đích" tùy theo hướng xe đã biết trước.
// ============================================================

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
RFIDReader rfid;
SpeedSensor irSensor;

enum GateState
{
    IDLE,
    WAITING_IR,
    CLOSING_DELAY
};
GateState gateState = IDLE;
String pendingUID = "";
String pendingAction = ""; // "IN" hoặc "OUT"
unsigned long delayStartMs = 0;

unsigned long lastTelemetryMs = 0;
unsigned long lastWifiRetryMs = 0;

// ── WiFi ─────────────────────────────────────────────────────
void connectWiFi(const String &ssid, const String &pass)
{
    Serial.printf("[WIFI] Connecting to %s ...\n", ssid.c_str());
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid.c_str(), pass.c_str());
    unsigned long t = millis();
    while (WiFi.status() != WL_CONNECTED && millis() - t < 15000)
    {
        delay(500);
        Serial.print(".");
    }
    Serial.println();
    if (WiFi.status() == WL_CONNECTED)
        Serial.printf("[WIFI] Connected - IP: %s\n", WiFi.localIP().toString().c_str());
    else
        Serial.println("[WIFI] Failed");
}

// ── Mở Barie + bắt đầu theo dõi IR theo hướng ────────────────
void openGate(const String &uid, const String &action)
{
    pendingUID = uid;
    pendingAction = action; // "IN" hoặc "OUT"

    // PHẦN CỨNG TRƯỚC
    barrier.open();
    SpeedSensor::Direction dir = (action == "OUT") ? SpeedSensor::DIR_OUT
                                                   : SpeedSensor::DIR_IN;
    irSensor.startWatch(dir);
    gateState = WAITING_IR;

    // Firebase SAU
    firebase.setGateStatus("open");

    Serial.printf("[GATE] Mở Barie — UID: %s, hướng: %s\n", uid.c_str(), action.c_str());
}

void onVehiclePassed(bool confirmed)
{
    // PHẦN CỨNG TRƯỚC — luôn đóng Barie dù confirmed hay không
    if (confirmed)
    {
        if (pendingAction == "IN")
            // barrier.playAudio(AUDIO_WELCOME); // <--- TẠM TẮT DÒNG NÀY
            Serial.println("[AUDIO-DEBUG] Bỏ qua âm thanh IN");
        else
            // barrier.playAudio(AUDIO_GOODBYE); // <--- TẠM TẮT DÒNG NÀY
            Serial.println("[AUDIO-DEBUG] Bỏ qua âm thanh OUT");
    }
    else
    {
        // barrier.playAudio(AUDIO_ERROR);       // <--- TẠM TẮT DÒNG NÀY
        Serial.println("[AUDIO-DEBUG] Bỏ qua âm thanh lỗi timeout");
    }

    gateState = CLOSING_DELAY;
    delayStartMs = millis();

    if (confirmed)
    {
        Serial.printf("[CONFIRM] Xe đã qua THẬT — %s UID: %s. Đóng sau %d ms\n",
                      pendingAction.c_str(), pendingUID.c_str(), GATE_CLOSE_DELAY_MS);
        // Firebase SAU — đây là nơi DUY NHẤT ghi isParking + parking_history
        firebase.commitParkingTransaction();
    }
    else
    {
        Serial.printf("[TIMEOUT] Không xác nhận được xe qua — UID: %s. HỦY giao dịch, không ghi Firestore.\n",
                      pendingUID.c_str());
        firebase.abortParkingTransaction();
    }
}

// ── Thực sự đóng Barie (sau độ trễ 0.5s) ─────────────────────
void closeGateNow()
{
    // PHẦN CỨNG TRƯỚC
    barrier.close();
    gateState = IDLE;

    Serial.println("[GATE] Đóng Barie — IDLE, sẵn sàng thẻ tiếp theo");

    // Firebase SAU
    firebase.setGateStatus("auto");

    pendingUID = "";
    pendingAction = "";
}

// ── Lệnh từ Cloud/App ────────────────────────────────────────
void onCloudCommand(String cmd, String uid, String action)
{
    if (cmd == "OPEN")
    {
        openGate(uid, action.length() ? action : "IN");
    }
    else if (cmd == "CLOSE")
    {
        irSensor.stopWatch();
        closeGateNow();
    }
    else if (cmd == "BUZZER_ALERT")
    {
        // barrier.playAudio(AUDIO_DEBT_EXCEED);
        Serial.println("[AUDIO-DEBUG] Bỏ qua âm thanh cảnh báo nợ vượt hạn mức");
    }
    else if (cmd == "CARD_UNKNOWN")
    {
        // barrier.playAudio(AUDIO_CARD_UNKNOWN);
        Serial.println("[AUDIO-DEBUG] Bỏ qua âm thanh thẻ chưa đăng ký");
    }
}

// ── Đổi WiFi từ xa ───────────────────────────────────────────
void handleRemoteWiFiChange()
{
    String newSSID, newPass;
    if (!firebase.checkRemoteWiFiChange(newSSID, newPass))
        return;

    Serial.printf("[WIFI] Remote switch -> %s\n", newSSID.c_str());
    WiFi.disconnect();
    delay(1000);
    connectWiFi(newSSID, newPass);

    bool ok = (WiFi.status() == WL_CONNECTED);
    firebase.reportWiFiSwitchResult(ok);
    if (!ok)
        connectWiFi(WIFI_SSID, WIFI_PASSWORD);
}

// ═══════════════════════════════════════════════════════════════
void setup()
{
    Serial.begin(115200);
    Serial.println("\n[BOOT] Smart Parking Node starting...");

    rfid.begin();
    irSensor.begin();
    barrier.begin();

    connectWiFi(WIFI_SSID, WIFI_PASSWORD);

    if (WiFi.status() == WL_CONNECTED)
    {
        firebase.begin();
        firebase.scanAndUploadNetworks();
    }

    Serial.println("[BOOT] Setup complete. Quẹt thẻ để test.");
}

// ═══════════════════════════════════════════════════════════════
void loop()
{

    // ── 1. Lệnh từ Cloud (polling) ───────────────────────────
    firebase.loop();

    // ── 2. RFID — chỉ đọc khi đang IDLE ───────────────────────
    if (gateState == IDLE)
    {
        String uid = rfid.readUID();
        if (!uid.isEmpty())
        {
            Serial.printf("[RFID] Card: %s\n", uid.c_str());
            // Hàm này tự gọi onCloudCommand("OPEN", uid, "IN"/"OUT" ...)
            firebase.processSwipeOnHardware(uid);
        }
    }

    // ── 3. Theo dõi IR đích duy nhất theo hướng ──────────────
    if (gateState == WAITING_IR)
    {
        SpeedSensor::Result r = irSensor.update();

        if (r == SpeedSensor::CONFIRMED)
        {
            onVehiclePassed(true); // xác nhận THẬT — sẽ commit Firestore
        }
        else if (r == SpeedSensor::TIMED_OUT)
        {
            onVehiclePassed(false); // timeout — đóng Barie nhưng KHÔNG commit
        }
    }

    // ── 4. Đóng Barie sau độ trễ 0.5s ─────────────────────────
    if (gateState == CLOSING_DELAY)
    {
        if (millis() - delayStartMs > GATE_CLOSE_DELAY_MS)
        {
            closeGateNow();
        }
    }

    // ── 5. Telemetry + check WiFi đổi từ xa ──────────────────
    if (millis() - lastTelemetryMs > TELEMETRY_INTERVAL_MS)
    {
        lastTelemetryMs = millis();
        firebase.sendTelemetry(true, true); // IR đang hoạt động bình thường
        handleRemoteWiFiChange();
    }

    // ── 6. WiFi watchdog ──────────────────────────────────────
    if (WiFi.status() != WL_CONNECTED)
    {
        if (millis() - lastWifiRetryMs > WIFI_RETRY_INTERVAL_MS)
        {
            lastWifiRetryMs = millis();
            Serial.println("[WIFI] Lost - retrying...");
            connectWiFi(WIFI_SSID, WIFI_PASSWORD);
            if (WiFi.status() == WL_CONNECTED)
            {
                firebase.begin();
                firebase.scanAndUploadNetworks();
            }
        }
    }

    delay(20);
}