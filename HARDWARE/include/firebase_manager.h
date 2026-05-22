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

class FirebaseManager {
private:
    FirebaseData   fbData;
    FirebaseConfig fbConfig;
    FirebaseAuth   fbAuth;
    bool           initialized = false;

    uint32_t dayTotalVehicles   = 0;
    uint32_t dayTotalViolations = 0;
    float    daySpeedSum        = 0.0f;
    String   currentDate        = "";

    unsigned long long getTimestampMs() {
        struct tm ti;
        if (!getLocalTime(&ti)) return (unsigned long long)millis();
        time_t t = mktime(&ti);
        return (unsigned long long)t * 1000ULL;
    }

    String getDateStr() {
        struct tm ti;
        if (!getLocalTime(&ti)) return "1970-01-01";
        char buf[12];
        strftime(buf, sizeof(buf), "%Y-%m-%d", &ti);
        return String(buf);
    }

    String getPeakHourStr() {
        struct tm ti;
        if (!getLocalTime(&ti)) return "00:00 - 01:00";
        char buf[20];
        snprintf(buf, sizeof(buf), "%02d:00 - %02d:00",
                 ti.tm_hour, (ti.tm_hour + 1) % 24);
        return String(buf);
    }

    void checkDayRollover() {
        String today = getDateStr();
        if (today != currentDate) {
            dayTotalVehicles   = 0;
            dayTotalViolations = 0;
            daySpeedSum        = 0.0f;
            currentDate        = today;
            DBGF("[Firebase] Ngày mới: %s — reset thống kê\n", today.c_str());
        }
    }

    String makeViolId() {
        char buf[12];
        snprintf(buf, sizeof(buf), "viol_%05lx",
                 (unsigned long)(millis() & 0xFFFFF));
        return String(buf);
    }

public:
    void begin() {
        fbConfig.host = FIREBASE_HOST;
        fbConfig.signer.tokens.legacy_token = FIREBASE_AUTH;

        Firebase.begin(&fbConfig, &fbAuth);
        Firebase.reconnectWiFi(true);
        
        initialized = true;
        currentDate = getDateStr();
        updateSystemStatus("online");
        DBGLN("[Firebase] Khởi tạo xong.");
    }

    bool pushVehicleEvent(const char* vehicleId, float speed, bool isViolation) {
        if (!initialized || WiFi.status() != WL_CONNECTED) return false;

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
        if (isViolation) {
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
        if (isViolation) dayTotalViolations++;
        updateDailyStats();

        return ok;
    }

    void updateDailyStats() {
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

    float fetchVmax(float currentVmax) {
        if (!initialized || WiFi.status() != WL_CONNECTED) return currentVmax;
 
        String srcPath  = "/system_config/" + String(NODE_ID) + "/v_max_threshold";
        // Đích: trường mới app dùng để đọc lại giá trị đang áp dụng
        String destPath = "/system_config/" + String(NODE_ID) + "/v_max";
 
        if (Firebase.getFloat(fbData, srcPath)) {
            float val = fbData.floatData();
            if (val > 0.0f && abs(val - currentVmax) > 0.1f) {
                // Đồng bộ giá trị mới vào v_max để app hiển thị
                Firebase.setFloat(fbData, destPath, val);
                DBGF("[Firebase] Vmax: %.1f → %.1f km/h (ghi vào v_max)\n",
                     currentVmax, val);
                return val;
            }
        }
        return currentVmax;
    }

    void updateSystemStatus(const char* status = "online") {
        if (!initialized || WiFi.status() != WL_CONNECTED) return;
        String path = "/system_config/" + String(NODE_ID);
        unsigned long long ts = getTimestampMs();

        FirebaseJson cfg;
        cfg.set("status", status);
        cfg.set("last_ping", (int64_t)ts);

        // SỬA: Gọi trực tiếp Firebase.updateNode
        Firebase.updateNode(fbData, path, cfg);
    }

    bool isReady() { return initialized; }
};

#endif