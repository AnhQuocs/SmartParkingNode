#pragma once

#include <Arduino.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
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

public:
    void begin()
    {
        fbConfig.host = FIREBASE_HOST;
        fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

        Firebase.begin(&fbConfig, &fbAuth);
        Firebase.reconnectWiFi(true);
        initialized = true;

        // Reset lệnh điều khiển từ cloud lúc khởi động
        Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");

        Serial.println("[Firebase] Đã khởi tạo cấu hình cho Smart Parking.");
    }

    // Hàm này được gọi liên tục trong loop() của main.cpp
    void loop()
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;

        // Cơ chế Polling lấy lệnh từ App/Cloud gửi xuống ESP32
        if (Firebase.getString(fbData, "/parking_status/cloud_command/cmd"))
        {
            String cmd = fbData.stringData();

            if (cmd != "" && cmd != "IDLE")
            {
                String uid = "";
                String action = "";

                if (Firebase.getString(fbData, "/parking_status/cloud_command/uid"))
                    uid = fbData.stringData();
                if (Firebase.getString(fbData, "/parking_status/cloud_command/action"))
                    action = fbData.stringData();

                // Clear lệnh ngay lập tức để không bị gọi lặp lại
                Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");

                // Kích hoạt hàm xử lý Barie/Loa bên main.cpp
                onCloudCommand(cmd, uid, action);
            }
        }
    }
    void processSwipeOnHardware(String uid)
    {
        if (!initialized)
            return;

        String path = "/authorized_cards/" + uid + "/isActive";

        // Cố gắng đọc giá trị isActive từ node UID
        if (Firebase.getBool(fbData, path))
        {
            bool isActive = fbData.boolData();

            if (isActive)
            {
                Serial.printf("[LOGIC] Thẻ %s hợp lệ. Mở Barie.\n", uid.c_str());
                // Gọi thẳng lệnh mở Barie
                onCloudCommand("OPEN", uid, "IN");
            }
            else
            {
                Serial.printf("[LOGIC] Thẻ %s đã bị khóa (isActive = false).\n", uid.c_str());
                logInvalidSwipe(uid, "BLOCKED");
                onCloudCommand("BUZZER_ALERT", uid, "");
            }
        }
        else
        {
            // Không tìm thấy node này trong authorized_cards -> Thẻ null / chưa đăng ký
            Serial.printf("[LOGIC] Thẻ %s chưa được đăng ký.\n", uid.c_str());
            logInvalidSwipe(uid, "UNKNOWN");
            onCloudCommand("CARD_UNKNOWN", uid, "");
        }
    }

    void logInvalidSwipe(String uid, String reason)
    {
        if (!initialized)
            return;

        FirebaseJson json;
        json.set("uid", uid);
        json.set("timestamp", (int64_t)millis());
        json.set("reason", reason); // "UNKNOWN" hoặc "BLOCKED"

        // Đẩy vào danh sách pending_cards hoặc logs từ chối
        String path = "/pending_cards/" + uid;
        Firebase.setJSON(fbData, path, json);
    }

    void sendSwipeEvent(String uid)
    {
        if (!initialized)
            return;
        FirebaseJson json;
        json.set("uid", uid);
        json.set("timestamp", (int64_t)millis()); // Dùng millis tạm thay cho NTP
        json.set("ir_confirmed", false);
        json.set("processed", false);

        String path = "/hardware_events/latest_swipe";
        Firebase.setJSON(fbData, path, json);
        Serial.printf("[Firebase] Đã đẩy sự kiện quẹt thẻ: %s\n", uid.c_str());
    }

    void setGateStatus(String status)
    {
        if (!initialized)
            return;
        Firebase.setString(fbData, "/parking_status/gate_control", status);
    }

    void confirmIR()
    {
        if (!initialized)
            return;
        // Báo cho Cloud biết xe đã thực sự qua cụm cảm biến hồng ngoại
        Firebase.setBool(fbData, "/hardware_events/latest_swipe/ir_confirmed", true);
    }

    void revertSwipe()
    {
        if (!initialized)
            return;
        // Hủy giao dịch (Timeout Revert) - Đánh dấu đã xử lý nhưng ir_confirmed là false
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
        if (ok)
        {
            Firebase.setString(fbNetData, path + "/message", "Connected IP: " + WiFi.localIP().toString());
        }
        else
        {
            Firebase.setString(fbNetData, path + "/message", "Connection Timeout");
        }
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

            // Thêm trường strength theo chuẩn JSON trong tài liệu
            if (rssi >= -50)
                net.set("strength", "Excellent");
            else if (rssi >= -65)
                net.set("strength", "Good");
            else if (rssi >= -75)
                net.set("strength", "Fair");
            else
                net.set("strength", "Weak");

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
        json.set("rfid_status", "OK"); // Hoặc lấy biến trạng thái RFID truyền vào

        int rssi = WiFi.RSSI();
        json.set("wifi_signal_pct", constrain(map(rssi, -100, -50, 0, 100), 0, 100));

        json.set("connection_status/wifi_status", "CONNECTED");
        json.set("connection_status/firebase_status", "AUTHENTICATED");
        json.set("connection_status/ip_address", WiFi.localIP().toString().c_str());

        Firebase.updateNode(fbData, path, json);
    }
};