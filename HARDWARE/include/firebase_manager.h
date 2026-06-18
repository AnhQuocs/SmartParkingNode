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
    struct PendingTx
    {
        bool active = false;
        String uid = "";
        String userId = "";
        String profileDocPath = ""; // đường dẫn đầy đủ document trong profiles
        String direction = "";      // "IN" hoặc "OUT"
        int64_t checkInTime = 0;    // chỉ có giá trị khi direction == OUT (lấy từ history đang mở)
        String openHistoryId = "";  // document ID của history_xxx đang CHECK_IN, để update khi OUT
    } _pending;

public:
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
                    auto fields = doc[0]["document"]["fields"];
                    bool isActive = fields["isActive"]["booleanValue"] | false;
                    bool isParking = fields["isParking"]["booleanValue"] | false;
                    String userId = fields["userId"]["stringValue"] | "";
                    String docName = doc[0]["document"]["name"] | ""; // full path

                    if (isActive)
                    {
                        // Hướng xác định theo isParking trong profiles (KHÔNG đảo ngay)
                        String direction = isParking ? "OUT" : "IN";

                        // Lưu tạm — CHƯA ghi/đảo gì lên Firestore.
                        // Chỉ commit thật khi IR xác nhận xe đã đi qua (xem commitParkingTransaction).
                        _pending.active = true;
                        _pending.uid = uid;
                        _pending.userId = userId;
                        _pending.profileDocPath = docName;
                        _pending.direction = direction;

                        if (direction == "OUT")
                        {
                            // Cần tìm history đang mở (status=CHECK_IN) để lấy checkInTime + doc id
                            _findOpenHistory(userId, uid);
                        }

                        Serial.printf("[LOGIC] Thẻ %s hợp lệ. Hướng: %s (chưa commit). Mở Barie.\n",
                                      uid.c_str(), direction.c_str());
                        onCloudCommand("OPEN", uid, direction);
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

    void _findOpenHistory(const String &userId, const String &uid)
    {
        HTTPClient http;
        http.setTimeout(4000);
        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents:runQuery?key=" +
                     String(FIREBASE_WEB_API_KEY);
        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        // Tìm theo rfidUid + status == CHECK_IN
        String payload =
            "{\"structuredQuery\":{"
            "\"from\":[{\"collectionId\":\"parking_history\"}],"
            "\"where\":{\"compositeFilter\":{\"op\":\"AND\",\"filters\":["
            "{\"fieldFilter\":{\"field\":{\"fieldPath\":\"rfidUid\"},\"op\":\"EQUAL\","
            "\"value\":{\"stringValue\":\"" +
            uid + "\"}}},"
                  "{\"fieldFilter\":{\"field\":{\"fieldPath\":\"status\"},\"op\":\"EQUAL\","
                  "\"value\":{\"stringValue\":\"CHECK_IN\"}}}"
                  "]}},"
                  "\"limit\":1"
                  "}}";

        int code = http.POST(payload);
        if (code > 0)
        {
            String resp = http.getString();
            if (resp.indexOf("\"document\":") > 0)
            {
                DynamicJsonDocument doc(2048);
                if (!deserializeJson(doc, resp))
                {
                    String fullPath = doc[0]["document"]["name"] | "";
                    // fullPath dạng .../documents/parking_history/history_003
                    int idx = fullPath.lastIndexOf('/');
                    _pending.openHistoryId = (idx >= 0) ? fullPath.substring(idx + 1) : "";
                    int64_t checkIn = doc[0]["document"]["fields"]["checkInTime"]["integerValue"] | (int64_t)0;
                    _pending.checkInTime = checkIn;
                    Serial.printf("[LOGIC] Tìm thấy history mở: %s, checkInTime=%lld\n",
                                  _pending.openHistoryId.c_str(), checkIn);
                }
            }
            else
            {
                Serial.println("[LOGIC] CẢNH BÁO: không tìm thấy history CHECK_IN tương ứng cho OUT");
            }
        }
        http.end();
    }

    void commitParkingTransaction()
    {
        if (!_pending.active)
        {
            Serial.println("[LOGIC] commitParkingTransaction gọi nhưng không có pending — bỏ qua");
            return;
        }

        int64_t nowTs = (int64_t)(millis() / 1000); // TODO: thay bằng NTP/epoch thật nếu có

        // 1. Đảo isParking trong profiles
        _patchProfileIsParking(_pending.profileDocPath, _pending.direction == "IN");

        // 2. Ghi parking_history
        if (_pending.direction == "IN")
        {
            _createCheckInHistory(_pending.userId, _pending.uid, nowTs);
        }
        else
        {
            _updateCheckOutHistory(_pending.openHistoryId, nowTs);
        }

        Serial.printf("[COMMIT-FIRESTORE] Hoàn tất ghi nhận %s cho UID %s\n",
                      _pending.direction.c_str(), _pending.uid.c_str());

        _pending = PendingTx(); // reset sạch
    }

    void abortParkingTransaction()
    {
        if (_pending.active)
        {
            Serial.printf("[ABORT] Hủy giao dịch UID %s — không ghi Firestore\n", _pending.uid.c_str());
        }
        _pending = PendingTx();
    }

private:
    // PATCH chỉ field isParking trong profiles, dùng updateMask
    void _patchProfileIsParking(const String &fullDocPath, bool newIsParking)
    {
        if (fullDocPath.isEmpty())
        {
            Serial.println("[FIRESTORE] Thiếu profileDocPath — không thể patch isParking");
            return;
        }
        HTTPClient http;
        http.setTimeout(4000);
        // fullDocPath đã là URL đầy đủ dạng projects/.../documents/profiles/xxx
        String url = "https://firestore.googleapis.com/v1/" + fullDocPath +
                     "?updateMask.fieldPaths=isParking&key=" + String(FIREBASE_WEB_API_KEY);

        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String body = String("{\"fields\":{\"isParking\":{\"booleanValue\":") +
                      (newIsParking ? "true" : "false") + "}}}";

        int code = http.PATCH(body);
        if (code != 200)
        {
            Serial.printf("[FIRESTORE] Patch isParking lỗi HTTP %d\n", code);
        }
        else
        {
            Serial.printf("[FIRESTORE] isParking -> %s\n", newIsParking ? "true" : "false");
        }
        http.end();
    }

    // Tạo document mới trong parking_history với status=CHECK_IN
    void _createCheckInHistory(const String &userId, const String &uid, int64_t nowTs)
    {
        HTTPClient http;
        http.setTimeout(4000);
        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents/parking_history?key=" +
                     String(FIREBASE_WEB_API_KEY);
        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String body =
            "{\"fields\":{"
            "\"userId\":{\"stringValue\":\"" +
            userId + "\"},"
                     "\"rfidUid\":{\"stringValue\":\"" +
            uid + "\"},"
                  "\"checkInTime\":{\"integerValue\":" +
            String((long)nowTs) + "},"
                                  "\"checkOutTime\":{\"nullValue\":null},"
                                  "\"durationMinutes\":{\"integerValue\":0},"
                                  "\"fee\":{\"doubleValue\":0.0},"
                                  "\"status\":{\"stringValue\":\"CHECK_IN\"},"
                                  "\"createdAt\":{\"integerValue\":" +
            String((long)nowTs) + "},"
                                  "\"updatedAt\":{\"integerValue\":" +
            String((long)nowTs) + "}"
                                  "}}";

        int code = http.POST(body);
        if (code != 200)
        {
            Serial.printf("[FIRESTORE] Tạo parking_history lỗi HTTP %d\n", code);
        }
        else
        {
            Serial.println("[FIRESTORE] Đã tạo parking_history (CHECK_IN)");
        }
        http.end();
    }

    // Update document parking_history đã có sẵn -> CHECK_OUT, tính fee/duration
    void _updateCheckOutHistory(const String &historyId, int64_t nowTs)
    {
        if (historyId.isEmpty())
        {
            Serial.println("[FIRESTORE] Thiếu historyId — không thể ghi CHECK_OUT");
            return;
        }

        int64_t durationSec = nowTs - _pending.checkInTime;
        int64_t durationMin = durationSec / 60;
        double fee = _calcFee(durationMin); // TODO: đồng bộ công thức tính phí với Quốc

        HTTPClient http;
        http.setTimeout(4000);
        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents/parking_history/" +
                     historyId +
                     "?updateMask.fieldPaths=checkOutTime"
                     "&updateMask.fieldPaths=durationMinutes"
                     "&updateMask.fieldPaths=fee"
                     "&updateMask.fieldPaths=status"
                     "&updateMask.fieldPaths=updatedAt"
                     "&key=" +
                     String(FIREBASE_WEB_API_KEY);
        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String body =
            "{\"fields\":{"
            "\"checkOutTime\":{\"integerValue\":" +
            String((long)nowTs) + "},"
                                  "\"durationMinutes\":{\"integerValue\":" +
            String((long)durationMin) + "},"
                                        "\"fee\":{\"doubleValue\":" +
            String(fee) + "},"
                          "\"status\":{\"stringValue\":\"CHECK_OUT\"},"
                          "\"updatedAt\":{\"integerValue\":" +
            String((long)nowTs) + "}"
                                  "}}";

        int code = http.PATCH(body);
        if (code != 200)
        {
            Serial.printf("[FIRESTORE] Update CHECK_OUT lỗi HTTP %d\n", code);
        }
        else
        {
            Serial.printf("[FIRESTORE] Đã CHECK_OUT — duration=%lld phút, fee=%.0f\n", durationMin, fee);
        }
        http.end();
    }

    // TODO: thay bằng đúng công thức của Quốc (pricing_config: grace_period,
    // base_rate, overnight_surcharge...). Đây là công thức tạm để test.
    double _calcFee(int64_t durationMin)
    {
        if (durationMin < 15)
            return 0.0;
        return 5000.0; // base_rate tạm, Quốc sẽ thay bằng logic đầy đủ
    }

public:
    // (Đã loại bỏ sendSwipeEvent / confirmIR / revertSwipe kiểu RTDB cũ —
    //  giờ dùng commitParkingTransaction() / abortParkingTransaction()
    //  ghi trực tiếp Firestore, xem phía trên.)

    void setGateStatus(String status)
    {
        if (!initialized)
            return;
        if (!Firebase.setString(fbData, "/parking_status/gate_control", status))
        {
            Serial.printf("[Firebase] setGateStatus lỗi: %s\n", fbData.errorReason().c_str());
        }
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
