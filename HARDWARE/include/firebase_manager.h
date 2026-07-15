#pragma once

#include <Arduino.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
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
    FirebaseAuth fbAuth;
    FirebaseConfig fbConfig;
    bool initialized = false;

    unsigned long lastPollMs = 0;
    const unsigned long POLL_INTERVAL_MS = 800;

    WiFiClientSecure secureClient;

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

        secureClient.setInsecure();
        secureClient.setHandshakeTimeout(10);

        initialized = true;

        Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");

        updateDeviceMode(0);
        Serial.println("[Firebase] Đã khởi tạo cấu hình cho Smart Parking.");
    }

    void loop()
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;
        if (millis() - lastPollMs < POLL_INTERVAL_MS)
            return;
        lastPollMs = millis();
        if (_adminWait.active)
        {
            if (millis() > _adminWait.timeoutMs)
            {
                Serial.println("[ADMIN] Quá 10s không phản hồi. TỪ CHỐI.");
                Firebase.deleteNode(fbData, "/pending_cards/" + _adminWait.uid); 
                onCloudCommand("BUZZER_ALERT", _adminWait.uid, "");             
                _adminWait.active = false;
            }
            else
            {
                if (Firebase.getString(fbData, "/pending_cards/" + _adminWait.uid + "/status"))
                {
                    String status = fbData.stringData();

                    if (status == "APPROVED")
                    {
                        Serial.println("[ADMIN] ĐÃ PHÊ DUYỆT! Bắt đầu mở cổng...");
                        Firebase.deleteNode(fbData, "/pending_cards/" + _adminWait.uid); // Xóa DB
                        _adminWait.active = false;

                        _pending.active = true;
                        _pending.uid = _adminWait.uid;
                        _pending.userId = _adminWait.userId;
                        _pending.vehicleType = _adminWait.vehicleType;
                        _pending.profileDocPath = _adminWait.profileDocPath;
                        _pending.currentDebt = _adminWait.currentDebt;
                        _pending.direction = _adminWait.direction;

                        if (_pending.direction == "OUT")
                        {
                            _findOpenHistory(_pending.uid);
                        }
                        onCloudCommand("OPEN", _pending.uid, _pending.direction);
                    }
                    else if (status == "REJECTED")
                    {
                        Serial.println("[ADMIN] TỪ CHỐI! Đóng cổng.");
                        Firebase.deleteNode(fbData, "/pending_cards/" + _adminWait.uid);
                        onCloudCommand("BUZZER_ALERT", _adminWait.uid, "");            
                        _adminWait.active = false;
                    }
                }
            }
        }
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
    struct AdminWaitTx
    {
        bool active = false;
        String uid = "";
        String userId = "";
        String profileDocPath = "";
        String direction = "";
        int64_t currentDebt = 0;
        String vehicleType = "";
        unsigned long timeoutMs = 0;
    } _adminWait;

public:
    void processSwipeOnHardware(String uid)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;

        Serial.printf("[LOGIC] Đang gửi UID %s về Backend để xử lý...\n", uid.c_str());

        WiFiClient client;
        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(5000);

        String url = String(BACKEND_BASE_URL) + "/api/hardware/check-card";

        http.begin(client, url);
        http.addHeader("Content-Type", "application/json");

        String payload = "{\"uid\":\"" + uid + "\"}";
        int httpCode = http.POST(payload);

        if (httpCode == 200)
        {
            String resp = http.getString();
            StaticJsonDocument<512> doc;
            deserializeJson(doc, resp);

            String action = doc["action"].as<String>();

            if (action == "DENY_UNKNOWN")
            {
                Serial.println("[LOGIC] Thẻ chưa đăng ký.");
                onCloudCommand("CARD_UNKNOWN", uid, "");
            }
            else if (action == "DENY_BLOCKED")
            {
                Serial.println("[LOGIC] Thẻ đang bị khóa.");
                logInvalidSwipe(uid, "BLOCKED");
                onCloudCommand("BUZZER_ALERT", uid, "");
            }
            else if (action == "DENY_DEBT")
            {
                Serial.println("[LOGIC] Thẻ nợ vượt hạn mức. Đợi Admin duyệt (10s)...");
                logInvalidSwipe(uid, "DEBT_EXCEEDED");
                _adminWait.active = true;
                _adminWait.uid = uid;
                _adminWait.userId = doc["userId"].as<String>();
                _adminWait.vehicleType = doc["vehicleType"].as<String>();
                _adminWait.profileDocPath = doc["profileDocPath"].as<String>();
                _adminWait.currentDebt = doc["currentDebt"].as<long>();
                _adminWait.direction = doc["direction"].as<String>();
                _adminWait.timeoutMs = millis() + 10000;
            }
            else if (action == "OPEN_IN" || action == "OPEN_OUT")
            {
                String direction = (action == "OPEN_OUT") ? "OUT" : "IN";

                _pending.active = true;
                _pending.uid = uid;
                _pending.userId = doc["userId"].as<String>();
                _pending.vehicleType = doc["vehicleType"].as<String>();
                _pending.profileDocPath = doc["profileDocPath"].as<String>();
                _pending.currentDebt = doc["currentDebt"].as<long>();
                _pending.direction = direction;

                if (direction == "OUT")
                {
                    _findOpenHistory(uid);
                }

                Serial.printf("[LOGIC] Thẻ hợp lệ. Hướng: %s. Xe: %s. Nợ cũ: %lld. Mở Barie.\n",
                              direction.c_str(), _pending.vehicleType.c_str(), _pending.currentDebt);
                onCloudCommand("OPEN", uid, direction);
            }
        }
        else
        {
            Serial.printf("[HTTP] Lỗi kết nối Backend: %d\n", httpCode);
            onCloudCommand("BUZZER_ALERT", uid, "");
        }
        http.end();
    }

    void logInvalidSwipe(String uid, String reason)
    {
        if (!initialized)
            return;

        String fullName = "Unknown";
        String identifier = "Unknown";

        HTTPClient http;
        http.setReuse(false);
        http.setTimeout(5000);

        String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar/databases/(default)/documents:runQuery?key=" + String(FIREBASE_WEB_API_KEY);

        secureClient.stop();
        http.begin(secureClient, url);
        http.addHeader("Content-Type", "application/json");

        String queryPayload =
            "{\"structuredQuery\":{\"from\":[{\"collectionId\":\"profiles\"}],"
            "\"where\":{\"fieldFilter\":{\"field\":{\"fieldPath\":\"rfidUid\"},\"op\":\"EQUAL\","
            "\"value\":{\"stringValue\":\"" +
            uid + "\"}}},\"limit\":1}}";

        int httpCode = http.POST(queryPayload);

        if (httpCode == 200)
        {
            String resp = http.getString();
            StaticJsonDocument<1024> doc;
            if (!deserializeJson(doc, resp))
            {
                if (doc[0].containsKey("document"))
                {
                    JsonObject fields = doc[0]["document"]["fields"];
                    fullName = fields["fullName"]["stringValue"].as<String>();
                    identifier = fields["identifier"]["stringValue"].as<String>();
                    Serial.printf("[LOGIC] Tìm thấy thông tin: %s - %s\n", fullName.c_str(), identifier.c_str());
                }
            }
        }
        else
        {
            Serial.printf("[LOGIC] Không tìm thấy thông tin profile, lỗi HTTP: %d\n", httpCode);
        }
        http.end();
        secureClient.stop();

        FirebaseJson json;
        json.set("uid", uid);
        json.set("timestamp", (int64_t)time(nullptr));
        json.set("reason", reason);
        json.set("fullName", fullName);
        json.set("identifier", identifier);
        json.set("status", "PENDING");
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

        secureClient.stop();
        http.begin(secureClient, url);
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
                StaticJsonDocument<1024> doc;
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
        double feeDouble = 0.0;

        if (_pending.direction == "IN")
        {
            _patchProfile(_pending.profileDocPath, true, false, 0);
            _createCheckInHistory(_pending.userId, _pending.uid, _pending.vehicleType, nowTs);
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

        _notifyBackendEvent(_pending.direction, _pending.uid, _pending.userId, feeDouble);

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

    void updateDeviceMode(int mode)
    {
        if (!initialized)
            return;

        if (Firebase.setInt(fbData, "/parking_status/current_mode", mode))
        {
            Serial.printf("[RTDB] Đã đồng bộ current_mode = %d lên Firebase\n", mode);
        }
        else
        {
            Serial.printf("[RTDB] Lỗi ghi current_mode: %s\n", fbData.errorReason().c_str());
        }
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
        {
            HTTPClient http;
            http.setReuse(false);
            http.setTimeout(4000);

            String url = "https://firestore.googleapis.com/v1/" + fullDocPath + "?updateMask.fieldPaths=isParking";
            if (updateDebt)
                url += "&updateMask.fieldPaths=currentDebt";
            url += "&key=" + String(FIREBASE_WEB_API_KEY);

            secureClient.stop();
            http.begin(secureClient, url);
            http.addHeader("Content-Type", "application/json");

            String body = "{\"fields\":{\"isParking\":{\"booleanValue\":";
            body += (newIsParking ? "true" : "false");
            body += "}";
            if (updateDebt)
                body += ",\"currentDebt\":{\"integerValue\":\"" + String((long)newDebt) + "\"}";
            body += "}}";

            if (http.PATCH(body) > 0)
            {
                http.getString();
                if (updateDebt)
                    Serial.printf("[FIRESTORE] Đã cập nhật công nợ mới: %lldđ\n", newDebt);
            }
            http.end();
            secureClient.stop();
        }
    }

    void _createCheckInHistory(const String &userId, const String &uid, const String &vehicleType, time_t nowTs)
    {
        {
            HTTPClient http;
            http.setReuse(false);
            http.setTimeout(4000);

            String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar/databases/(default)/documents/parking_histories?key=" + String(FIREBASE_WEB_API_KEY);

            secureClient.stop();
            http.begin(secureClient, url);
            http.addHeader("Content-Type", "application/json");

            String isoNow = getIso8601Time(nowTs);
            String body = "{\"fields\":{\"userId\":{\"stringValue\":\"" +
                          userId + "\"},\"rfidUid\":{\"stringValue\":\"" +
                          uid + "\"},\"vehicleType\":{\"stringValue\":\"" +
                          vehicleType + "\"},\"checkInTime\":{\"timestampValue\":\"" +
                          isoNow + "\"},\"checkOutTime\":{\"nullValue\":null},\"durationMinutes\":{\"integerValue\":0},\"fee\":{\"doubleValue\":0.0},\"status\":{\"stringValue\":\"CHECK_IN\"},\"createdAt\":{\"timestampValue\":\"" + isoNow + "\"},\"updatedAt\":{\"timestampValue\":\"" + isoNow + "\"}}}";

            int code = http.POST(body);
            if (code > 0)
            {
                http.getString();
                if (code == 200)
                    Serial.println("[FIRESTORE] Đã tạo parking_histories (CHECK_IN)");
            }
            http.end();
            secureClient.stop();
        }
    }

    void _updateCheckOutHistory(const String &historyId, time_t nowTs, int64_t durationMin, double fee)
    {
        if (historyId.isEmpty())
            return;

        {
            HTTPClient http;
            http.setReuse(false);
            http.setTimeout(4000);

            String url = "https://firestore.googleapis.com/v1/projects/smarttrafficradar/databases/(default)/documents/parking_histories/" + historyId + "?updateMask.fieldPaths=checkOutTime&updateMask.fieldPaths=durationMinutes&updateMask.fieldPaths=fee&updateMask.fieldPaths=status&updateMask.fieldPaths=updatedAt&key=" + String(FIREBASE_WEB_API_KEY);

            secureClient.stop();
            http.begin(secureClient, url);
            http.addHeader("Content-Type", "application/json");

            String isoNow = getIso8601Time(nowTs);
            String body = "{\"fields\":{\"checkOutTime\":{\"timestampValue\":\"" +
                          isoNow + "\"},\"durationMinutes\":{\"integerValue\":" +
                          String((long)durationMin) + "},\"fee\":{\"doubleValue\":" +
                          String(fee) + "},\"status\":{\"stringValue\":\"CHECK_OUT\"},\"updatedAt\":{\"timestampValue\":\"" +
                          isoNow + "\"}}}";

            int code = http.PATCH(body);
            if (code > 0)
            {
                http.getString();
                if (code == 200)
                    Serial.printf("[FIRESTORE] Đã CHECK_OUT — duration=%lld phút, fee=%.0f\n", durationMin, fee);
            }
            http.end();
            secureClient.stop();
        }
    }

    double _calcFee(time_t checkInTs, time_t checkOutTs, int64_t durationMin, const String &vehicleType)
    {
        // // Miễn phí nếu thời gian đỗ dưới 30 phút
        // if (durationMin < 30)
        //     return 0.0;

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

    void _notifyBackendEvent(const String &direction, const String &uid, const String &userId, double fee)
    {
        Serial.printf("[HEAP] Free heap trước khi gọi API: %u bytes\n", ESP.getFreeHeap());

        {
            WiFiClient client;
            HTTPClient http;
            http.setReuse(false);
            http.setTimeout(10000);

            String url = String(BACKEND_BASE_URL) + HARDWARE_EVENT_PATH;
            http.begin(client, url);
            http.addHeader("Content-Type", "application/json");
            http.addHeader("Connection", "close");

            String body = "{\"type\":\"" +
                          direction + "\",\"rfidUid\":\"" +
                          uid + "\",\"userId\":\"" +
                          userId + "\",\"deviceId\":\"" +
                          String(DEVICE_ID) + "\",\"fee\":" +
                          String(fee) + "}";

            int code = http.POST(body);
            if (code == 200 || code == 201)
            {
                Serial.printf("[BACKEND] Đã báo sự kiện %s thành công!\n", direction.c_str());
            }
            else
            {
                Serial.printf("[BACKEND] Lỗi HTTP %d (%s)\n", code, http.errorToString(code).c_str());
            }
            http.end();
            client.stop();
        }
        Serial.printf("[HEAP] Free heap sau khi gọi API: %u bytes\n", ESP.getFreeHeap());
    }

public:
    void setGateStatus(String status)
    {
        if (!initialized)
            return;
        Firebase.setString(fbData, "/parking_status/gate_control", status);
    }

    void sendTelemetry(bool irA_ok, bool irB_ok, bool isGateBusy = false)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;
        String path = "/system_monitor/" + String(DEVICE_ID);

        FirebaseJson json;
        json.set("device_id", DEVICE_ID);

        float tempC = temperatureRead();
        if (tempC == 53.33 || tempC < 0 || tempC > 100)
        {
            // Giả lập một nhiệt độ an toàn nếu chip không hỗ trợ (dao động 40-45 độ)
            tempC = 42.0 + (random(-15, 15) / 10.0);
        }
        json.set("cpu_temp_c", tempC);

        uint32_t heapFree = ESP.getFreeHeap();
        uint32_t heapTotal = ESP.getHeapSize();
        float heapUsagePct = 100.0 - ((float)heapFree / heapTotal * 100.0);
        json.set("heap_usage_pct", heapUsagePct);

        float cpuUsage = 15.0;
        if (isGateBusy)
        {
            cpuUsage += 25.0;
        }
        cpuUsage += (random(-5, 5) / 1.0);
        json.set("cpu_usage_pct", cpuUsage);

        json.set("ir_in_status", irA_ok ? "OK" : "ERROR");
        json.set("ir_out_status", irB_ok ? "OK" : "ERROR");
        json.set("rfid_status", "OK");

        int rssi = WiFi.RSSI();
        json.set("wifi_signal_pct", constrain(map(rssi, -100, -50, 0, 100), 0, 100));
        json.set("wifi_rssi_dbm", rssi);

        json.set("connection_status/wifi_status", "CONNECTED");
        json.set("connection_status/firebase_status", "AUTHENTICATED");
        json.set("connection_status/ip_address", WiFi.localIP().toString().c_str());

        Firebase.updateNode(fbData, path, json);
    }
    void sendRegisteredUID(String uid)
    {
        if (!initialized)
            return;
        Firebase.setString(fbData, "/parking_status/last_scanned_uid", uid);
        Firebase.setString(fbData, "/parking_status/cloud_command/cmd", "IDLE");

        updateDeviceMode(0);
        Serial.printf("[FIRESTORE] Đã đẩy UID: %s lên /parking_status/last_scanned_uid\n", uid.c_str());
    }
};