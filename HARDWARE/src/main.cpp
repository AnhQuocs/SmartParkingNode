
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
    CONFIRMED
};
GateState gateState = IDLE;
String pendingUID = "";
String pendingAction = ""; // "IN" hoáº·c "OUT"
unsigned long stateTs = 0;

unsigned long lastTelemetryMs = 0;
unsigned long lastWifiRetryMs = 0;

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
        Serial.printf("[WIFI] Connected â€” IP: %s\n", WiFi.localIP().toString().c_str());
    else
        Serial.println("[WIFI] Failed");
}

void onCloudCommand(String cmd, String uid, String action)
{
    pendingUID = uid;
    pendingAction = action;

    if (cmd == "OPEN")
    {
        barrier.open();
        firebase.setGateStatus("open");
        irSensor.reset();
        gateState = WAITING_IR;
        stateTs = millis();
    }
    else if (cmd == "CLOSE")
    {
        barrier.close();
        firebase.setGateStatus("auto");
        gateState = IDLE;
    }
    else if (cmd == "BUZZER_ALERT")
    {
        barrier.playAudio(AUDIO_DEBT_EXCEED);
    }
    else if (cmd == "CARD_UNKNOWN")
    {
        barrier.playAudio(AUDIO_CARD_UNKNOWN);
    }
}

void commitTransaction()
{
    firebase.confirmIR();

    if (pendingAction == "IN")
        barrier.playAudio(AUDIO_WELCOME);
    else
        barrier.playAudio(AUDIO_GOODBYE);

    gateState = CONFIRMED;
    stateTs = millis();
    Serial.printf("[COMMIT] %s â€” UID: %s\n", pendingAction.c_str(), pendingUID.c_str());
}

void revertTransaction()
{
    barrier.close();
    firebase.revertSwipe();
    firebase.setGateStatus("auto");

    irSensor.reset();
    pendingUID = "";
    pendingAction = "";
    gateState = IDLE;
    Serial.println("[REVERT] No log written.");
}

void handleRemoteWiFiChange()
{
    String newSSID, newPass;
    if (!firebase.checkRemoteWiFiChange(newSSID, newPass))
        return;

    Serial.printf("[WIFI] Remote switch â†’ %s\n", newSSID.c_str());
    WiFi.disconnect();
    delay(1000);
    connectWiFi(newSSID, newPass);

    bool ok = (WiFi.status() == WL_CONNECTED);
    firebase.reportWiFiSwitchResult(ok);

    if (!ok)
    {
        Serial.println("[WIFI] Reverting to default WiFi");
        connectWiFi(WIFI_SSID, WIFI_PASSWORD);
    }
}

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

    Serial.println("[BOOT] Setup complete.");
}

void loop()
{

    firebase.loop();

    if (gateState == IDLE)
    {
        String uid = rfid.readUID();
        if (!uid.isEmpty())
        {
            Serial.printf("[RFID] Card: %s\n", uid.c_str());
            firebase.processSwipeOnHardware(uid);
        }
    }

    if (gateState == WAITING_IR)
    {
        if (irSensor.update())
        {
            commitTransaction();
        }
        else
        {
            if (irSensor.isDetecting())
            {
                stateTs = millis();
            }

            // Nếu không có xe che quá 10 giây thì mới hủy giao dịch
            if (millis() - stateTs > TIMEOUT_REVERT_MS)
            {
                Serial.println("[TIMEOUT] Reverting... No vehicle passed.");
                revertTransaction();
            }
        }
    }

    if (gateState == CONFIRMED)
    {
        if (millis() - stateTs > GATE_AUTO_CLOSE_MS)
        {
            barrier.close();
            firebase.setGateStatus("auto");
            pendingUID = "";
            pendingAction = "";
            irSensor.reset();
            gateState = IDLE;
            Serial.println("[GATE] Barrier closed — IDLE, ready for next card");
        }
    }

    if (millis() - lastTelemetryMs > TELEMETRY_INTERVAL_MS)
    {
        lastTelemetryMs = millis();
        bool irA_ok = (digitalRead(PIN_IR_A) != -1);
        bool irB_ok = (digitalRead(PIN_IR_B) != -1);
        firebase.sendTelemetry(irA_ok, irB_ok);
        handleRemoteWiFiChange();
    }

    if (WiFi.status() != WL_CONNECTED)
    {
        if (millis() - lastWifiRetryMs > WIFI_RETRY_INTERVAL_MS)
        {
            lastWifiRetryMs = millis();
            Serial.println("[WIFI] Lost â€” retrying...");
            connectWiFi(WIFI_SSID, WIFI_PASSWORD);
            if (WiFi.status() == WL_CONNECTED)
            {
                firebase.begin();
                firebase.scanAndUploadNetworks();
            }
        }
    }

    delay(50);
}