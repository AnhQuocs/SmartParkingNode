#pragma once

// ============================================================
//  firebase_manager.h — Giao tiếp Firebase RTDB + Firestore REST
// ============================================================

#include <Arduino.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include <ArduinoJson.h>
#include <HTTPClient.h>
#include <time.h>
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
    const unsigned long POLL_INTERVAL_MS = 800;

public:
    void begin()
    {
        Serial.print("\n[TIME] Đang đồng bộ máy chủ thời gian NTP...");
        configTime(0, 0, "pool.ntp.org", "time.nist.gov", "time.windows.com");

        time_t now = time(nullptr);
        int retries = 0;

        while (now < 1600000000)
        {
            delay(1000);
            Serial.print(".");
            now = time(nullptr);
            retries++;
            if (retries % 10 == 0)
            {
                configTime(0, 0, "pool.ntp.org", "time.nist.gov");
            }
        }

        struct tm timeinfo;
        gmtime_r(&now, &timeinfo);
        Serial.printf("\n[TIME] Đồng bộ thành công! Ngày: %d-%02d-%02d\n",
                      timeinfo.tm_year + 1900, timeinfo.tm_mon + 1, timeinfo.tm_mday);

        fbConfig.host = FIREBASE_HOST;
        fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

        Firebase.begin(&fbConfig, &fbAuth);
        Firebase.reconnectWiFi(true);

        fbData.setBSSLBufferSize(2048, 1024);
        fbData.setResponseSize(2048);

        initialized = true;

        Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");
        Serial.println("[Firebase] Đã khởi tạo cấu hình cho Smart Parking.");
    }

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
        String profileDocPath = "";
        String direction = "";
        time_t checkInTime = 0;
        String openHistoryId = "";
        int64_t currentDebt = 0;
        String vehicleType = "";
    } _pending;

public:
    void processSwipeOnHardware(String uid)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;

        Serial.printf("[LOGIC] Đang query thẻ %s trên Cloud Firestore...\n", uid.c_str());

        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(4000);
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
            if (httpCode == 200)
            {
                DynamicJsonDocument doc(2048);
                DeserializationError err = deserializeJson(doc, payload);

                if (!err && doc[0].containsKey("document"))
                {
                    JsonObjectConst fields = doc[0]["document"]["fields"];
                    bool isActive = fields["isActive"]["booleanValue"] | false;
                    bool isParking = fields["isParking"]["booleanValue"] | false;

                    int64_t currentDebt = 0;
                    if (fields["currentDebt"]["integerValue"])
                    {
                        currentDebt = fields["currentDebt"]["integerValue"].as<long>();
                    }

                    String vehicleType = fields["vehicleType"]["stringValue"] | "MOTORBIKE";

                    String profileUid = fields["uid"]["stringValue"] | "";
                    if (profileUid == "")
                        profileUid = fields["userId"]["stringValue"] | "";
                    if (profileUid == "")
                        profileUid = uid;

                    String docName = doc[0]["document"]["name"] | "";

                    if (isActive)
                    {
                        // <-- LOGIC KIỂM TRA HẠN MỨC NỢ TẠI ĐÂY -->
                        if (currentDebt > 100000)
                        {
                            Serial.printf("[LOGIC] Thẻ %s bị từ chối. Dư nợ hiện tại (%lldđ) vượt quá hạn mức 100.000đ.\n", uid.c_str(), currentDebt);
                            logInvalidSwipe(uid, "DEBT_EXCEEDED");   // Lưu lịch sử quẹt lỗi lên Firebase
                            onCloudCommand("BUZZER_ALERT", uid, ""); // Báo còi / phát loa AUDIO_DEBT_EXCEED
                        }
                        else
                        {
                            // Nợ hợp lệ -> Xử lý mở cổng bình thường
                            String direction = isParking ? "OUT" : "IN";

                            _pending.active = true;
                            _pending.uid = uid;
                            _pending.userId = profileUid;
                            _pending.profileDocPath = docName;
                            _pending.direction = direction;
                            _pending.currentDebt = currentDebt;
                            _pending.vehicleType = vehicleType;

                            if (direction == "OUT")
                            {
                                _findOpenHistory(uid);
                            }

                            Serial.printf("[LOGIC] Thẻ %s hợp lệ. Xe: %s. Hướng: %s. Nợ cũ: %lld. Mở Barie.\n",
                                          uid.c_str(), vehicleType.c_str(), direction.c_str(), currentDebt);
                            onCloudCommand("OPEN", uid, direction);
                        }
                    }
                    else
                    {
                        Serial.printf("[LOGIC] Thẻ %s bị khóa.\n", uid.c_str());
                        logInvalidSwipe(uid, "BLOCKED");
                        onCloudCommand("BUZZER_ALERT", uid, ""); // Tái sử dụng âm thanh cảnh báo lỗi
                    }
                }
                else
                {
                    Serial.printf("[LOGIC] Thẻ %s chưa đăng ký.\n", uid.c_str());
                    logInvalidSwipe(uid, "UNKNOWN");
                    onCloudCommand("CARD_UNKNOWN", uid, "");
                }
            }
        }
        else
        {
            Serial.printf("[HTTP] Lỗi kết nối Firestore: %d\n", httpCode);
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

    void _findOpenHistory(const String &uid)
    {
        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(4000);
        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents:runQuery?key=" +
                     String(FIREBASE_WEB_API_KEY);
        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String payload =
            "{\"structuredQuery\":{"
            "\"from\":[{\"collectionId\":\"parking_histories\"}],"
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
            if (code == 200)
            {
                DynamicJsonDocument doc(2048);
                if (!deserializeJson(doc, resp) && doc[0].containsKey("document"))
                {
                    String fullPath = doc[0]["document"]["name"] | "";
                    int idx = fullPath.lastIndexOf('/');
                    _pending.openHistoryId = (idx >= 0) ? fullPath.substring(idx + 1) : "";

                    String isoCheckIn = doc[0]["document"]["fields"]["checkInTime"]["timestampValue"] | "";
                    _pending.checkInTime = parseIso8601(isoCheckIn);
                }
            }
        }
        http.end();
    }

    void commitParkingTransaction()
    {
        if (!_pending.active)
            return;

        time_t nowTs = time(nullptr);

        if (_pending.direction == "IN")
        {
            _patchProfile(_pending.profileDocPath, true, false, 0);
            _createCheckInHistory(_pending.userId, _pending.uid, nowTs);
        }
        else
        {
            int64_t durationSec = nowTs - _pending.checkInTime;
            if (durationSec < 0)
                durationSec = 0;
            int64_t durationMin = durationSec / 60;

            double feeDouble = _calcFee(_pending.checkInTime, nowTs, durationMin, _pending.vehicleType);
            int64_t feeInt = (int64_t)feeDouble;

            int64_t newDebt = _pending.currentDebt + feeInt;

            _patchProfile(_pending.profileDocPath, false, true, newDebt);
            _updateCheckOutHistory(_pending.openHistoryId, nowTs, durationMin, feeDouble);
        }

        Serial.printf("[COMMIT-FIRESTORE] Hoàn tất ghi nhận %s cho UID %s\n",
                      _pending.direction.c_str(), _pending.uid.c_str());

        _pending = PendingTx();
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
    String getIso8601Time(time_t now)
    {
        if (now < 100000)
            return "1970-01-01T00:00:00Z";
        struct tm timeinfo;
        gmtime_r(&now, &timeinfo);
        char buf[30];
        strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%SZ", &timeinfo);
        return String(buf);
    }

    time_t parseIso8601(const String &iso)
    {
        if (iso.isEmpty())
            return 0;
        struct tm t;
        memset(&t, 0, sizeof(tm));
        sscanf(iso.c_str(), "%d-%d-%dT%d:%d:%d", &t.tm_year, &t.tm_mon, &t.tm_mday, &t.tm_hour, &t.tm_min, &t.tm_sec);
        t.tm_year -= 1900;
        t.tm_mon -= 1;
        return mktime(&t);
    }

    void _patchProfile(const String &fullDocPath, bool newIsParking, bool updateDebt, int64_t newDebt)
    {
        if (fullDocPath.isEmpty())
            return;
        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(4000);

        String url = "https://firestore.googleapis.com/v1/" + fullDocPath +
                     "?updateMask.fieldPaths=isParking";

        if (updateDebt)
        {
            url += "&updateMask.fieldPaths=currentDebt";
        }
        url += "&key=" + String(FIREBASE_WEB_API_KEY);

        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String body = "{\"fields\":{\"isParking\":{\"booleanValue\":";
        body += (newIsParking ? "true" : "false");
        body += "}";

        if (updateDebt)
        {
            body += ",\"currentDebt\":{\"integerValue\":\"" + String((long)newDebt) + "\"}";
        }
        body += "}}";

        if (http.PATCH(body) > 0)
        {
            http.getString();
            if (updateDebt)
            {
                Serial.printf("[FIRESTORE] Đã cập nhật công nợ mới: %lldđ\n", newDebt);
            }
        }
        http.end();
    }

    void _createCheckInHistory(const String &userId, const String &uid, time_t nowTs)
    {
        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(4000);

        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents/parking_histories?key=" +
                     String(FIREBASE_WEB_API_KEY);
        http.begin(url);
        http.addHeader("Content-Type", "application/json");

        String isoNow = getIso8601Time(nowTs);

        String body =
            "{\"fields\":{"
            "\"userId\":{\"stringValue\":\"" +
            userId + "\"},"
                     "\"rfidUid\":{\"stringValue\":\"" +
            uid + "\"},"
                  "\"checkInTime\":{\"timestampValue\":\"" +
            isoNow + "\"},"
                     "\"checkOutTime\":{\"nullValue\":null},"
                     "\"durationMinutes\":{\"integerValue\":0},"
                     "\"fee\":{\"doubleValue\":0.0},"
                     "\"status\":{\"stringValue\":\"CHECK_IN\"},"
                     "\"createdAt\":{\"timestampValue\":\"" +
            isoNow + "\"},"
                     "\"updatedAt\":{\"timestampValue\":\"" +
            isoNow + "\"}"
                     "}}";

        int code = http.POST(body);
        if (code > 0)
        {
            http.getString();
            if (code == 200)
                Serial.println("[FIRESTORE] Đã tạo parking_histories (CHECK_IN)");
        }
        http.end();
    }

    void _updateCheckOutHistory(const String &historyId, time_t nowTs, int64_t durationMin, double fee)
    {
        if (historyId.isEmpty())
            return;

        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(4000);

        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar"
                     "/databases/(default)/documents/parking_histories/" +
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

        String isoNow = getIso8601Time(nowTs);

        String body =
            "{\"fields\":{"
            "\"checkOutTime\":{\"timestampValue\":\"" +
            isoNow + "\"},"
                     "\"durationMinutes\":{\"integerValue\":" +
            String((long)durationMin) + "},"
                                        "\"fee\":{\"doubleValue\":" +
            String(fee) + "},"
                          "\"status\":{\"stringValue\":\"CHECK_OUT\"},"
                          "\"updatedAt\":{\"timestampValue\":\"" +
            isoNow + "\"}"
                     "}}";

        int code = http.PATCH(body);
        if (code > 0)
        {
            http.getString();
            if (code == 200)
            {
                Serial.printf("[FIRESTORE] Đã CHECK_OUT — duration=%lld phút, fee=%.0f\n", durationMin, fee);
            }
        }
        http.end();
    }

    double _calcFee(time_t checkInTs, time_t checkOutTs, int64_t durationMin, const String &vehicleType)
    {
        // Miễn phí nếu thời gian đỗ dưới 30 phút
        if (durationMin <= 30)
            return 0.0;

        time_t inLocal = checkInTs + 7 * 3600;
        time_t outLocal = checkOutTs + 7 * 3600;

        long inDay = inLocal / 86400;
        long outDay = outLocal / 86400;

        // Số đêm = Ngày ra - Ngày vào
        long overnightCount = outDay - inDay;

        if (overnightCount > 0)
        {
            // Xe để qua đêm nhiều ngày -> Nhân phí với số đêm
            if (vehicleType == "CAR")
                return 50000.0 * overnightCount;
            return 15000.0 * overnightCount;
        }
        else
        {
            // Xe ra vào trong cùng 1 ngày
            if (vehicleType == "CAR")
                return 20000.0;
            return 5000.0;
        }
    }

public:
    void setGateStatus(String status)
    {
        if (!initialized)
            return;
        Firebase.setString(fbData, "/parking_status/gate_control", status);
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
            net.set("strength", rssi >= -50 ? "Excellent" : rssi >= -65 ? "Good"
                                                        : rssi >= -75   ? "Fair"
                                                                        : "Weak");
            Firebase.setJSON(fbData, path + "/net_" + String(i), net);
        }
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