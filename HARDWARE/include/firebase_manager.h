#pragma once

// ============================================================
//  firebase_manager.h — Giao tiếp với Firebase RTDB
//  - Gửi sự kiện quẹt thẻ lên hardware_events
//  - Stream lệnh từ Cloud/Android qua gate_control
//  - Gửi telemetry lên system_monitor
//  - Đổi WiFi từ xa qua network_setup
// ============================================================

#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include "config.h"
#include "secrets.h"

// Callback được định nghĩa trong main.cpp
extern void onCloudCommand(String cmd, String uid, String action);

class FirebaseManager {
public:
    // ── Init ─────────────────────────────────────────────────
    void begin() {
        _fbConfig.api_key      = FIREBASE_API_KEY;
        _fbConfig.database_url = FIREBASE_DATABASE_URL;
        _fbConfig.token_status_callback = tokenStatusCallback;

        Firebase.signUp(&_fbConfig, &_fbAuth, "", "");
        Firebase.begin(&_fbConfig, &_fbAuth);
        Firebase.reconnectWiFi(true);

        _ready = true;
        Serial.println("[FIREBASE] Initialized");

        _setupStream();
    }

    bool isReady() { return _ready && WiFi.status() == WL_CONNECTED; }

    // ── Stream loop (gọi trong loop()) ───────────────────────
    void loop() {
        if (_ready) Firebase.RTDB.runStream(&_fbStream);
    }

    // ── Gửi sự kiện quẹt thẻ ────────────────────────────────
    void sendSwipeEvent(const String &uid) {
        if (!isReady()) { Serial.println("[FB] Offline — swipe ignored"); return; }

        FirebaseJson payload;
        payload.set("uid",          uid);
        payload.set("timestamp",    (int)millis());
        payload.set("ir_confirmed", false);
        payload.set("processed",    false);
        payload.set("device_id",    DEVICE_ID);

        String path = String("/hardware_events/") + DEVICE_ID + "/latest_swipe";
        if (Firebase.RTDB.setJSON(&_fbdo, path.c_str(), &payload)) {
            Serial.printf("[FB] Swipe sent: %s\n", uid.c_str());
        } else {
            Serial.printf("[FB] Swipe failed: %s\n", _fbdo.errorReason().c_str());
        }
    }

    // ── Xác nhận IR (xe đã qua) ──────────────────────────────
    void confirmIR() {
        if (!isReady()) return;
        String path = String("/hardware_events/") + DEVICE_ID + "/latest_swipe/ir_confirmed";
        Firebase.RTDB.setBool(&_fbdo, path.c_str(), true);
        Serial.println("[FB] IR confirmed");
    }

    // ── Hủy giao dịch (timeout revert) ───────────────────────
    void revertSwipe() {
        if (!isReady()) return;
        FirebaseJson payload;
        payload.set("processed",    true);
        payload.set("ir_confirmed", false);
        payload.set("reverted",     true);

        String path = String("/hardware_events/") + DEVICE_ID + "/latest_swipe";
        Firebase.RTDB.updateNode(&_fbdo, path.c_str(), &payload);
        Serial.println("[FB] Swipe reverted");
    }

    // ── Cập nhật trạng thái barrier ──────────────────────────
    void setGateStatus(const String &status) {
        if (!isReady()) return;
        Firebase.RTDB.setString(&_fbdo, "/parking_status/gate_control", status.c_str());
    }

    // ── Telemetry ─────────────────────────────────────────────
    void sendTelemetry(bool irA_ok, bool irB_ok) {
        if (!isReady()) return;

        extern uint8_t temprature_sens_read();
        float tempC    = (temprature_sens_read() - 32) / 1.8f;
        float heapPct  = 100.0f - (ESP.getFreeHeap() * 100.0f / ESP.getHeapSize());
        int   wifiPct  = min(max(2 * (WiFi.RSSI() + 100), 0), 100);

        FirebaseJson doc;
        doc.set("device_id",       DEVICE_ID);
        doc.set("cpu_temp_c",      tempC);
        doc.set("heap_usage_pct",  heapPct);
        doc.set("ir_in_status",    irA_ok ? "OK" : "FAULT");
        doc.set("ir_out_status",   irB_ok ? "OK" : "FAULT");
        doc.set("rfid_status",     "OK");
        doc.set("wifi_signal_pct", wifiPct);

        FirebaseJson conn;
        conn.set("wifi_status",     WiFi.status() == WL_CONNECTED ? "CONNECTED" : "DISCONNECTED");
        conn.set("firebase_status", _ready ? "AUTHENTICATED" : "DISCONNECTED");
        conn.set("ip_address",      WiFi.localIP().toString());
        doc.set("connection_status", conn);

        String path = String("/system_monitor/") + DEVICE_ID;
        if (Firebase.RTDB.updateNode(&_fbdo, path.c_str(), &doc)) {
            Serial.println("[FB] Telemetry sent");
        } else {
            Serial.printf("[FB] Telemetry failed: %s\n", _fbdo.errorReason().c_str());
        }
    }

    // ── Scan & upload danh sách WiFi ─────────────────────────
    void scanAndUploadNetworks() {
        int n = WiFi.scanNetworks();
        if (n <= 0) return;

        FirebaseJson nets;
        for (int i = 0; i < min(n, 10); i++) {
            int    sigPct   = min(max(2 * (WiFi.RSSI(i) + 100), 0), 100);
            String strength = sigPct >= 80 ? "Excellent"
                            : sigPct >= 60 ? "Good"
                            : sigPct >= 40 ? "Fair" : "Weak";
            FirebaseJson net;
            net.set("ssid",       WiFi.SSID(i));
            net.set("rssi",       WiFi.RSSI(i));
            net.set("signal_pct", sigPct);
            net.set("strength",   strength);
            nets.set("net_" + String(i), net);
        }
        WiFi.scanDelete();

        String path = String("/system_monitor/") + DEVICE_ID + "/available_networks";
        Firebase.RTDB.setJSON(&_fbdo, path.c_str(), &nets);
        Serial.printf("[FB] Scanned %d networks\n", n);
    }

    // ── Kiểm tra lệnh đổi WiFi từ xa ────────────────────────
    // Trả về {ssid, password} nếu có lệnh SWITCHING, ngược lại trả ""
    bool checkRemoteWiFiChange(String &outSSID, String &outPassword) {
        if (!isReady()) return false;

        String path = String("/network_setup/") + DEVICE_ID;
        if (!Firebase.RTDB.getJSON(&_fbdo, path.c_str())) return false;

        FirebaseJson     &json = _fbdo.jsonObject();
        FirebaseJsonData  result;

        json.get(result, "status");
        if (result.stringValue != "SWITCHING") return false;

        json.get(result, "ssid");     outSSID     = result.stringValue;
        json.get(result, "password"); outPassword = result.stringValue;
        return !outSSID.isEmpty();
    }

    void reportWiFiSwitchResult(bool success) {
        if (!isReady()) return;
        FirebaseJson payload;
        payload.set("status",  success ? "CONNECTED" : "FAILED");
        payload.set("message", success ? "Connected successfully" : "Cannot connect — reverted");
        String path = String("/network_setup/") + DEVICE_ID;
        Firebase.RTDB.updateNode(&_fbdo, path.c_str(), &payload);
    }

private:
    FirebaseData   _fbdo;
    FirebaseData   _fbStream;
    FirebaseAuth   _fbAuth;
    FirebaseConfig _fbConfig;
    bool           _ready = false;

    void _setupStream() {
        String path = String("/gate_control/") + DEVICE_ID;
        if (!Firebase.RTDB.beginStream(&_fbStream, path.c_str())) {
            Serial.printf("[STREAM] Failed: %s\n", _fbStream.errorReason().c_str());
            return;
        }
        Firebase.RTDB.setStreamCallback(&_fbStream, _streamCb, _streamTimeoutCb);
        Serial.println("[STREAM] Listening on gate_control/" DEVICE_ID);
    }

    static void _streamCb(FirebaseStream data) {
        if (data.dataTypeEnum() != fb_esp_rtdb_data_type_json) return;

        FirebaseJson    &json = data.jsonObject();
        FirebaseJsonData result;

        String cmd, uid, action;
        if (json.get(result, "command")) cmd    = result.stringValue;
        if (json.get(result, "uid"))     uid    = result.stringValue;
        if (json.get(result, "action"))  action = result.stringValue;

        if (!cmd.isEmpty()) {
            Serial.printf("[STREAM] Command: %s\n", cmd.c_str());
            onCloudCommand(cmd, uid, action);
        }
    }

    static void _streamTimeoutCb(bool timeout) {
        if (timeout) Serial.println("[STREAM] Timeout — resuming...");
    }
};
