/**
 * firebase_manager.h - Đã sửa lỗi truy cập RTDB Private
 */

#ifndef FIREBASE_MANAGER_H
#define FIREBASE_MANAGER_H

#include <Arduino.h>
#include <FirebaseESP32.h>
#include "config.h"

#ifndef DBGLN
#define DBGLN(x) Serial.println(x)
#define DBGF(...) Serial.printf(__VA_ARGS__)
#endif

class FirebaseManager
{
private:
    FirebaseData fbData;
    FirebaseConfig fbConfig;
    FirebaseAuth fbAuth;
    bool initialized = false;

    uint32_t dayTotalVehicles = 0;
    uint32_t dayTotalViolations = 0;
    float daySpeedSum = 0.0f;
    String currentDate = "";

    unsigned long long getTimestampMs()
    {
        struct tm ti;
        if (!getLocalTime(&ti))
            return (unsigned long long)millis();
        time_t t = mktime(&ti);
        return (unsigned long long)t * 1000ULL;
    }

    String getDateStr()
    {
        struct tm ti;
        if (!getLocalTime(&ti))
            return "1970-01-01";
        char buf[12];
        strftime(buf, sizeof(buf), "%Y-%m-%d", &ti);
        return String(buf);
    }

    String getPeakHourStr()
    {
        struct tm ti;
        if (!getLocalTime(&ti))
            return "00:00 - 01:00";
        char buf[20];
        snprintf(buf, sizeof(buf), "%02d:00 - %02d:00",
                 ti.tm_hour, (ti.tm_hour + 1) % 24);
        return String(buf);
    }

    void checkDayRollover()
    {
        String today = getDateStr();
        if (today != currentDate)
        {
            dayTotalVehicles = 0;
            dayTotalViolations = 0;
            daySpeedSum = 0.0f;
            currentDate = today;
            DBGF("[Firebase] Ngày mới: %s — reset thống kê\n", today.c_str());
        }
    }

    String makeViolId()
    {
        char buf[12];
        snprintf(buf, sizeof(buf), "viol_%05lx",
                 (unsigned long)(millis() & 0xFFFFF));
        return String(buf);
    }

public:
    void begin()
    {
        fbConfig.host = FIREBASE_HOST;
        fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

        Firebase.begin(&fbConfig, &fbAuth);
        Firebase.reconnectWiFi(true);

        initialized = true;
        currentDate = getDateStr();
        updateSystemStatus("online");
        DBGLN("[Firebase] Khởi tạo xong.");
    }

    bool pushVehicleEvent(const char *vehicleId, float speed, bool isViolation)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return false;

        checkDayRollover();
        unsigned long long ts = getTimestampMs();
        String base = "/live_tracking/" + String(NODE_ID);

        // ── 1. latest_vehicle ──
        FirebaseJson latest;
        latest.set("vehicle_id", vehicleId);
        latest.set("speed_kmh", (double)speed);
        latest.set("is_violation", isViolation);
        latest.set("timestamp", (int64_t)ts);

        // SỬA: Gọi trực tiếp Firebase.updateNode (không có .RTDB, không dùng dấu & cho object)
        bool ok = Firebase.updateNode(fbData, base + "/latest_vehicle", latest);

        // ── 2. recent_vehicles ──
        String recentKey = String("v") + String((uint32_t)(ts & 0xFFFFFFFF));
        FirebaseJson recent;
        recent.set("speed_kmh", (double)speed);
        recent.set("is_violation", isViolation);
        recent.set("timestamp", (int64_t)ts);

        // SỬA: Gọi trực tiếp Firebase.setJSON
        Firebase.setJSON(fbData, base + "/recent_vehicles/" + recentKey, recent);

        // ── 3. violations ──
        if (isViolation)
        {
            String violId = makeViolId();
            String violPath = "/violations/" + String(NODE_ID) + "/" + violId;
            FirebaseJson viol;
            viol.set("vehicle_id", vehicleId);
            viol.set("speed_kmh", (double)speed);
            viol.set("timestamp", (int64_t)ts);
            viol.set("resolved", false);

            // SỬA: Gọi trực tiếp Firebase.setJSON
            Firebase.setJSON(fbData, violPath, viol);
        }

        // ── 4. daily_stats ──
        dayTotalVehicles++;
        daySpeedSum += speed;
        if (isViolation)
            dayTotalViolations++;
        updateDailyStats();

        return ok;
    }

    void updateDailyStats()
    {
        String path = "/daily_stats/" + String(NODE_ID) + "/" + currentDate;
        float avg = (dayTotalVehicles > 0) ? (daySpeedSum / (float)dayTotalVehicles) : 0.0f;
        float avgRounded = (float)((int)(avg * 10)) / 10.0f;

        FirebaseJson stats;
        stats.set("total_vehicles", (int)dayTotalVehicles);
        stats.set("total_violations", (int)dayTotalViolations);
        stats.set("average_speed", (double)avgRounded);
        stats.set("peak_hour", getPeakHourStr().c_str());

        // SỬA: Gọi trực tiếp Firebase.updateNode
        Firebase.updateNode(fbData, path, stats);
    }

    float fetchVmax(float currentVmax)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return currentVmax;
        String path = "/system_config/" + String(NODE_ID) + "/v_max_threshold";

        // SỬA: Gọi trực tiếp Firebase.getFloat
        if (Firebase.getFloat(fbData, path))
        {
            float val = fbData.floatData();
            if (val > 0.0f && abs(val - currentVmax) > 0.1f)
            {
                return val;
            }
        }
        return currentVmax;
    }

    void updateSystemStatus(const char *status = "online")
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return;
        String path = "/system_config/" + String(NODE_ID);
        unsigned long long ts = getTimestampMs();

        FirebaseJson cfg;
        cfg.set("status", status);
        cfg.set("last_ping", (int64_t)ts);

        // SỬA: Gọi trực tiếp Firebase.updateNode
        Firebase.updateNode(fbData, path, cfg);
    }

    // ─────────────────────────────────────────────
    //  NETWORK SWITCHING — An toàn, không mất Firebase
    //
    //  Flow đầy đủ:
    //  1. App ghi ssid + password + switch_request=true lên Firebase
    //  2. ESP32 đọc credentials TRONG KHI vẫn còn kết nối WiFi cũ
    //  3. Ghi switch_status="SWITCHING" lên Firebase (app thấy ngay)
    //  4. Reset flag switch_request=false trước khi mất mạng
    //  5. Disconnect WiFi cũ → connect WiFi mới
    //  6. Ghi SUCCESS hoặc FAILED lên Firebase
    //  7. Nếu FAILED → tự fallback về WiFi cũ trong config.h
    // ─────────────────────────────────────────────

    /**
     * Kiểm tra app có yêu cầu đổi WiFi không
     * Đọc credentials TRƯỚC khi mất kết nối — quan trọng!
     */
    bool fetchNetworkConfig(String &newSsid, String &newPass)
    {
        if (!initialized || WiFi.status() != WL_CONNECTED)
            return false;

        String basePath = "/network_setup/" + String(NODE_ID);
        String ssid = "", pass = "";

        if (Firebase.getString(fbData, basePath + "/ssid"))
            ssid = fbData.stringData();
        if (Firebase.getString(fbData, basePath + "/password"))
            pass = fbData.stringData();

        // Bỏ qua nếu trống hoặc giống mạng hiện tại
        if (ssid.length() == 0 || pass.length() == 0)
            return false;
        if (ssid == WiFi.SSID())
            return false;

        newSsid = ssid;
        newPass = pass;
        DBGF("[Network] Phát hiện SSID mới: %s → %s\n",
             WiFi.SSID().c_str(), ssid.c_str());
        return true;
    }

    /**
     * Thực hiện switch WiFi an toàn với fallback
     * Gọi ngay sau fetchNetworkConfig() trả về true
     */
    bool performNetworkSwitch(const String &newSsid, const String &newPass)
    {
        String basePath = "/network_setup/" + String(NODE_ID);

        // Bước 1: Báo app "SWITCHING" — vẫn còn online lúc này
        pushSwitchStatus("SWITCHING", newSsid.c_str(), "Connecting to new WiFi...");

        // Bước 2: Disconnect WiFi cũ
        WiFi.disconnect(false);
        delay(500);

        // Bước 3: Connect WiFi mới — chờ tối đa 10 giây
        WiFi.begin(newSsid.c_str(), newPass.c_str());
        uint8_t tries = 0;
        while (WiFi.status() != WL_CONNECTED && tries < 20)
        {
            delay(500);
            DBG(".");
            tries++;
        }
        DBGLN("");

        if (WiFi.status() == WL_CONNECTED)
        {
            // ── THÀNH CÔNG ──
            DBGF("[Network] Switch OK → %s | IP: %s\n",
                 newSsid.c_str(), WiFi.localIP().toString().c_str());

            delay(1000);
            Firebase.reconnectWiFi(true);
            delay(500);

            pushSwitchStatus("SUCCESS", newSsid.c_str(),
                             ("Connected. IP: " + WiFi.localIP().toString()).c_str());
            pushConnectionStatus();
            return true;
        }
        else
        {
            // ── THẤT BẠI → fallback WiFi cũ trong config.h ──
            DBGF("[Network] Thất bại — fallback về %s\n", WIFI_SSID);

            WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
            tries = 0;
            while (WiFi.status() != WL_CONNECTED && tries < 20)
            {
                delay(500);
                tries++;
            }

            if (WiFi.status() == WL_CONNECTED)
            {
                DBGLN("[Network] Fallback OK");
                delay(500);
                Firebase.reconnectWiFi(true);
                pushSwitchStatus("FAILED", newSsid.c_str(), "Connection Timeout. Device is not responding.");
                pushConnectionStatus();
            }
            else
            {
                DBGLN("[Network] Fallback cũng thất bại!");
            }
            return false;
        }
    }

    /**
     * Ghi trạng thái switch lên Firebase
     * App realtime observe node này để hiển thị kết quả
     * status: "SWITCHING" | "SUCCESS" | "FAILED"
     */
    // Ghi thẳng vào /network_setup/{node} — cấu trúc phẳng
    // khớp Firebase thực tế: ssid, password, status, message, timestamp
    void pushSwitchStatus(const char *status,
                          const char *targetSsid,
                          const char *message)
    {
        if (!initialized)
            return;

        String path = "/network_setup/" + String(NODE_ID);
        unsigned long long ts = getTimestampMs();

        FirebaseJson sw;
        sw.set("ssid", targetSsid);
        sw.set("status", status);
        sw.set("message", message);
        sw.set("timestamp", (int64_t)ts);

        Firebase.updateNode(fbData, path, sw);
        DBGF("[Network] network_setup → %s | %s\n", status, message);
    }

    // connection_status nằm trong system_monitor, không tạo collection mới
    void pushConnectionStatus()
    {
        if (!initialized)
            return;

        String path = "/system_monitor/" + String(NODE_ID) + "/connection_status";
        unsigned long long ts = getTimestampMs();

        FirebaseJson cs;
        cs.set("wifi_status", WiFi.status() == WL_CONNECTED
                                  ? "CONNECTED"
                                  : "DISCONNECTED");
        cs.set("firebase_status", "AUTHENTICATED");
        cs.set("current_ssid", WiFi.SSID().c_str());
        cs.set("ip_address", WiFi.localIP().toString().c_str());
        cs.set("last_sync", (int64_t)ts);

        Firebase.updateNode(fbData, path, cs);
    }

    void pushWifiScanResults(int networkCount)
    {
        if (!initialized)
            return;

        // available_networks nằm trong system_monitor
        String path = "/system_monitor/" + String(NODE_ID) + "/available_networks";
        Firebase.deleteNode(fbData, path);

        for (int i = 0; i < networkCount; i++)
        {
            String netPath = path + "/net_" + String(i);
            int32_t rssi = WiFi.RSSI(i);
            int signalPct = constrain((int)map(rssi, -100, -50, 0, 100), 0, 100);

            const char *strength;
            if (rssi >= -50)
                strength = "Excellent";
            else if (rssi >= -65)
                strength = "Good";
            else if (rssi >= -75)
                strength = "Fair";
            else
                strength = "Weak";

            const char *security;
            switch (WiFi.encryptionType(i))
            {
            case WIFI_AUTH_OPEN:
                security = "Open";
                break;
            case WIFI_AUTH_WEP:
                security = "WEP";
                break;
            case WIFI_AUTH_WPA_PSK:
                security = "WPA";
                break;
            case WIFI_AUTH_WPA2_PSK:
                security = "WPA2";
                break;
            case WIFI_AUTH_WPA_WPA2_PSK:
                security = "WPA/WPA2";
                break;
            default:
                security = "Unknown";
                break;
            }

            FirebaseJson net;
            net.set("ssid", WiFi.SSID(i).c_str());
            net.set("rssi", (int)rssi);
            net.set("signal_pct", (int)signalPct);
            net.set("strength", strength);
            net.set("security", security);
            net.set("channel", (int)WiFi.channel(i));

            Firebase.setJSON(fbData, netPath, net);
            DBGF("[WiFiScan] %d. %s | %d dBm (%d%%) | %s\n",
                 i + 1, WiFi.SSID(i).c_str(), rssi, signalPct, security);
        }

        String metaPath = "/system_monitor/" + String(NODE_ID) + "/scan_meta";
        FirebaseJson meta;
        meta.set("total_found", (int)networkCount);
        meta.set("scanned_at", (int64_t)getTimestampMs());
        meta.set("scan_status", "DONE");
        Firebase.updateNode(fbData, metaPath, meta);
        DBGF("[WiFiScan] Đẩy %d mạng lên Firebase xong\n", networkCount);
    }

    bool isReady() { return initialized; }
    FirebaseData &getFbData() { return fbData; }
};

#endif