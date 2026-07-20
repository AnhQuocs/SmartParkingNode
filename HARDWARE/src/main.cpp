
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

bool isRegistrationMode = false;
unsigned long regModeTimeoutMs = 0;

String lastReadUID = "";
unsigned long lastReadTime = 0;

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
    gateState = CLOSING_DELAY;
    delayStartMs = millis();

    if (confirmed)
    {
        Serial.printf("[CONFIRM] Xe đã qua THẬT — %s UID: %s. Đóng sau %d ms\n",
                      pendingAction.c_str(), pendingUID.c_str(), GATE_CLOSE_DELAY_MS);
        // Ghi Firebase
        firebase.commitParkingTransaction();
    }
    else
    {
        Serial.printf("[TIMEOUT] Không xác nhận được xe qua — UID: %s. HỦY giao dịch, không ghi Firestore.\n",
                      pendingUID.c_str());
        firebase.abortParkingTransaction();
    }
}

// ── Thực sự đóng Barie ─────────────────────
void closeGateNow()
{
    barrier.close();
    gateState = IDLE;

    Serial.println("[GATE] Đóng Barie — IDLE, sẵn sàng thẻ tiếp theo");

    // Firebase SAU
    firebase.setGateStatus("close");

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
        Serial.println("[ALERT] Thẻ nợ quá hạn / Bị khóa!");
    }
    else if (cmd == "CARD_UNKNOWN")
    {
        Serial.println("[ALERT] Thẻ chưa đăng ký!");
    }
    else if (cmd == "REGISTER_MODE" || cmd == "MODE_1")
    {
        isRegistrationMode = true;
        regModeTimeoutMs = millis() + 30000;

        firebase.updateDeviceMode(1);
        Serial.println("[MODE] Đã chuyển sang MODE 1: ĐĂNG KÝ THẺ (30 giây)...");
    }
    else if (cmd == "NORMAL_MODE" || cmd == "MODE_0")
    {
        isRegistrationMode = false;

        firebase.updateDeviceMode(0);
        Serial.println("[MODE] Đã hủy đăng ký, quay về MODE 0: BÌNH THƯỜNG.");
    }
}

void setup()
{
    Serial.begin(115200);
    Serial.println("\n[BOOT] Smart Parking Node starting...");

    rfid.begin();
    irSensor.begin();
    barrier.begin(); // Gọi khởi tạo Cả Servo lẫn Audio

    connectWiFi(WIFI_SSID, WIFI_PASSWORD);

    if (WiFi.status() == WL_CONNECTED)
    {
        firebase.begin();
    }

    Serial.println("[BOOT] Setup complete. Quẹt thẻ để test.");
}

// ═══════════════════════════════════════════════════════════════
void loop()
{
    firebase.loop();

    if (gateState == IDLE)
    {
        String uid = rfid.readUID();
        if (!uid.isEmpty())
        {
            if (uid != lastReadUID || (millis() - lastReadTime > 3000))
            {
                lastReadUID = uid;
                lastReadTime = millis();

                Serial.printf("[RFID] Card: %s\n", uid.c_str());
                if (isRegistrationMode)
                {
                    Serial.println("[MODE] Đang trong chế độ Đăng ký. Bắn UID lên RTDB...");
                    firebase.sendRegisteredUID(uid); 
                    isRegistrationMode = false;      
                    firebase.updateDeviceMode(0);
                }
                else
                {
                    firebase.processSwipeOnHardware(uid);
                }
            }
        }
    }

    if (isRegistrationMode && (millis() > regModeTimeoutMs))
    {
        isRegistrationMode = false;
        Serial.println("[MODE] Hết hạn 30s. Đã tự động quay về MODE 0.");
    }

    if (gateState == WAITING_IR)
    {
        SpeedSensor::Result r = irSensor.update();

        if (r == SpeedSensor::CONFIRMED)
        {
            onVehiclePassed(true);
        }
        else if (r == SpeedSensor::TIMED_OUT)
        {
            onVehiclePassed(false);
        }
    }

    if (gateState == CLOSING_DELAY)
    {
        if (millis() - delayStartMs > GATE_CLOSE_DELAY_MS)
        {
            closeGateNow();
        }
    }

    if (millis() - lastTelemetryMs > TELEMETRY_INTERVAL_MS)
    {
        lastTelemetryMs = millis();
        firebase.sendTelemetry(true, true, gateState != IDLE); // IR đang hoạt động bình thường
    }

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
            }
        }
    }

    delay(20);
}