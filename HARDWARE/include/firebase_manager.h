#pragma once

#include <Arduino.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include "config.h"

extern void onCloudCommand(String cmd, String uid, String action);

class FirebaseManager
{
private:
    FirebaseData fbData;
    FirebaseData fbNetData;
    FirebaseAuth fbAuth;
    FirebaseConfig fbConfig;
    bool initialized = false;

    unsigned long lastPollMs = 0;
    const unsigned long POLL_INTERVAL_MS = 800; // tránh poll quá dày gây nghẽn

public:
    void begin()
    {
        fbConfig.host = FIREBASE_HOST;
        fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

        Firebase.begin(&fbConfig, &fbAuth);
        Firebase.reconnectWiFi(true);

        // Giảm timeout mặc định để không bao giờ treo quá lâu nếu mất mạng
        fbData.setBSSLBufferSize(2048, 1024);
        fbData.setResponseSize(2048);

        initialized = true;

        Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");
        Serial.println("[Firebase] Đã khởi tạo cấu hình cho Smart Parking.");
    }

    // Gọi liên tục trong loop() — đã giới hạn tần suất để tránh nghẽn
    void loop()
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;
        if (millis() - lastPollMs < POLL_INTERVAL_MS)
            return;
        lastPollMs = millis();

        if (Firebase.getString(fbData, "/parking_status/cloud_command/cmd"))
        {
            String cmd = fbData.stringData();
            if (cmd != "" && cmd != "IDLE")
            {
                String uid = "", action = "";
                if (Firebase.getString(fbData, "/parking_status/cloud_command/uid"))
                    uid = fbData.stringData();
                if (Firebase.getString(fbData, "/parking_status/cloud_command/action"))
                    action = fbData.stringData();

                Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");
                onCloudCommand(cmd, uid, action);
            }
        }
    }


    void processSwipeOnHardware(String uid)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;

        Serial.printf("[LOGIC] Đang query thẻ %s trên Cloud Firestore...\n", uid.c_str());

        HTTPClient http;
        http.setTimeout(4000); // QUAN TRỌNG: chặn treo vô hạn nếu mạng chậm
        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents:runQuery?key=" +
                     String(FIREBASE_WEB_API_KEY);

        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String queryPayload =
            "{\"structuredQuery\":{"
            "\"from\":[{\"collectionId\":\"profiles\"}],"
            "\"where\":{\"fieldFilter\":{"
            "\"field\":{\"fieldPath\":\"rfidUid\"},"
            "\"op\":\"EQUAL\","
            "\"value\":{\"stringValue\":\"" +
            uid + "\"}"
                  "}}}}";

        int httpCode = http.POST(queryPayload);

        if (httpCode > 0)
        {
            String payload = http.getString();

            if (payload.indexOf("\"document\":") > 0)
            {
                DynamicJsonDocument doc(2048);
                DeserializationError err = deserializeJson(doc, payload);

                if (!err)
                {
                    bool isActive = doc[0]["document"]["fields"]["isActive"]["booleanValue"];
                    if (isActive)
                    {
                        Serial.printf("[LOGIC] Thẻ %s hợp lệ. Mở Barie.\n", uid.c_str());
                        onCloudCommand("OPEN", uid, "IN");
                    }
                    else
                    {
                        Serial.printf("[LOGIC] Thẻ %s bị khóa.\n", uid.c_str());
                        logInvalidSwipe(uid, "BLOCKED");
                        onCloudCommand("BUZZER_ALERT", uid, "");
                    }
                }
                else
                {
                    Serial.println("[LOGIC] Lỗi parse JSON Firestore");
                }
            }
            else
            {
                Serial.printf("[LOGIC] Thẻ %s chưa đăng ký. Đẩy pending_cards.\n", uid.c_str());
                logInvalidSwipe(uid, "UNKNOWN");
                onCloudCommand("CARD_UNKNOWN", uid, "");
            }
        }
        else
        {
            Serial.printf("[HTTP] Lỗi kết nối Firestore: %d — bỏ qua lượt quẹt này\n", httpCode);
        }

        http.end();
    }

    void logInvalidSwipe(String uid, String reason)
    {
        if (!initialized)
            return;
        FirebaseJson json;
        json.set("uid", uid);
        json.set("timestamp", (int64_t)millis());
        json.set("reason", reason);
        Firebase.setJSON(fbData, "/pending_cards/" + uid, json);
    }

    // Gọi SAU khi servo/audio đã xử lý — không chặn flow phần cứng nếu lỗi
    void sendSwipeEvent(String uid)
    {
        if (!initialized)
            return;
        FirebaseJson json;
        json.set("uid", uid);
        json.set("timestamp", (int64_t)millis());
        json.set("ir_confirmed", false);
        json.set("processed", false);
        if (!Firebase.setJSON(fbData, "/hardware_events/latest_swipe", json))
        {
            Serial.printf("[Firebase] sendSwipeEvent lỗi: %s\n", fbData.errorReason().c_str());
        }
    }

    void setGateStatus(String status)
    {
        if (!initialized)
            return;
        if (!Firebase.setString(fbData, "/parking_status/gate_control", status))
        {
            Serial.printf("[Firebase] setGateStatus lỗi: %s\n", fbData.errorReason().c_str());
        }
    }

    void confirmIR()
    {
        if (!initialized)
            return;
        if (!Firebase.setBool(fbData, "/hardware_events/latest_swipe/ir_confirmed", true))
        {
            Serial.printf("[Firebase] confirmIR lỗi: %s\n", fbData.errorReason().c_str());
        }
    }

    void revertSwipe()
    {
        if (!initialized)
            return;
        Firebase.setBool(fbData, "/hardware_events/latest_swipe/processed", true);
    }

    bool checkRemoteWiFiChange(String &newSSID, String &newPass)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return false;
        String path = "/network_setup/" + String(DEVICE_ID);

        if (Firebase.getString(fbNetData, path + "/status"))
        {
            if (fbNetData.stringData() == "SWITCHING")
            {
                if (Firebase.getString(fbNetData, path + "/ssid"))
                    newSSID = fbNetData.stringData();
                if (Firebase.getString(fbNetData, path + "/password"))
                    newPass = fbNetData.stringData();
                return true;
            }
        }
        return false;
    }

    void reportWiFiSwitchResult(bool ok)
    {
        if (!initialized)
            return;
        String path = "/network_setup/" + String(DEVICE_ID);
        Firebase.setString(fbNetData, path + "/status", ok ? "SUCCESS" : "FAILED");
        Firebase.setString(fbNetData, path + "/message",
                           ok ? "Connected IP: " + WiFi.localIP().toString() : "Connection Timeout");
    }

    void scanAndUploadNetworks()
    {
        if (!initialized)
            return;
        int n = WiFi.scanNetworks();
        String path = "/system_monitor/" + String(DEVICE_ID) + "/available_networks";
        Firebase.deleteNode(fbData, path);

        for (int i = 0; i < n; ++i)
        {
            FirebaseJson net;
            int rssi = WiFi.RSSI(i);
            net.set("ssid", WiFi.SSID(i).c_str());
            net.set("rssi", rssi);
            net.set("signal_pct", constrain(map(rssi, -100, -50, 0, 100), 0, 100));
            net.set("strength",
                    rssi >= -50 ? "Excellent" : rssi >= -65 ? "Good"
                                            : rssi >= -75   ? "Fair"
                                                            : "Weak");
            Firebase.setJSON(fbData, path + "/net_" + String(i), net);
        }
        Serial.println("[Firebase] Cập nhật danh sách WiFi thành công.");
    }

    void sendTelemetry(bool irA_ok, bool irB_ok)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;
        String path = "/system_monitor/" + String(DEVICE_ID);

        FirebaseJson json;
        json.set("device_id", DEVICE_ID);
        json.set("cpu_temp_c", temperatureRead());

        uint32_t heapFree = ESP.getFreeHeap();
        uint32_t heapTotal = ESP.getHeapSize();
        json.set("heap_usage_pct", 100.0 - ((float)heapFree / heapTotal * 100.0));

        json.set("ir_in_status", irA_ok ? "OK" : "ERROR");
        json.set("ir_out_status", irB_ok ? "OK" : "ERROR");
        json.set("rfid_status", "OK");

        int rssi = WiFi.RSSI();
        json.set("wifi_signal_pct", constrain(map(rssi, -100, -50, 0, 100), 0, 100));
        json.set("connection_status/wifi_status", "CONNECTED");
        json.set("connection_status/firebase_status", "AUTHENTICATED");
        json.set("connection_status/ip_address", WiFi.localIP().toString().c_str());

        Firebase.updateNode(fbData, path, json);
    }
};
