package com.example.smarttrafficradar.features.system_monitor.data.dto

import com.google.firebase.database.PropertyName

data class SystemMonitorDto(
    @get:PropertyName("device_id") @set:PropertyName("device_id") @field:PropertyName("device_id")
    var deviceId: String? = null,
    
    @get:PropertyName("device_type") @set:PropertyName("device_type") @field:PropertyName("device_type")
    var deviceType: String? = null,
    
    var firmware: String? = null,
    var uptime: String? = null,
    
    @get:PropertyName("cpu_usage_pct") @set:PropertyName("cpu_usage_pct") @field:PropertyName("cpu_usage_pct")
    var cpuUsagePct: Int? = null,
    
    @get:PropertyName("cpu_temp_c") @set:PropertyName("cpu_temp_c") @field:PropertyName("cpu_temp_c")
    var cpuTempC: Double? = null,

    @get:PropertyName("cycle_time_ms") @set:PropertyName("cycle_time_ms") @field:PropertyName("cycle_time_ms")
    var cycleTimeMs: Int? = null,

    @get:PropertyName("db_status") @set:PropertyName("db_status") @field:PropertyName("db_status")
    var dbStatus: String? = null,

    var fps: Double? = null,

    @get:PropertyName("heap_free_bytes") @set:PropertyName("heap_free_bytes") @field:PropertyName("heap_free_bytes")
    var heapFreeBytes: Long? = null,
    
    @get:PropertyName("heap_usage_pct") @set:PropertyName("heap_usage_pct") @field:PropertyName("heap_usage_pct")
    var heapUsagePct: Double? = null,

    @get:PropertyName("ir1_status") @set:PropertyName("ir1_status") @field:PropertyName("ir1_status")
    var ir1Status: String? = null,

    @get:PropertyName("ir2_status") @set:PropertyName("ir2_status") @field:PropertyName("ir2_status")
    var ir2Status: String? = null,

    @get:PropertyName("ping_firebase_ms") @set:PropertyName("ping_firebase_ms") @field:PropertyName("ping_firebase_ms")
    var pingFirebaseMs: Int? = null,

    @get:PropertyName("reconnect_count") @set:PropertyName("reconnect_count") @field:PropertyName("reconnect_count")
    var reconnectCount: Int? = null,

    @get:PropertyName("rfid_status") @set:PropertyName("rfid_status") @field:PropertyName("rfid_status")
    var rfidStatus: String? = null,

    @get:PropertyName("wifi_rssi_dbm") @set:PropertyName("wifi_rssi_dbm") @field:PropertyName("wifi_rssi_dbm")
    var wifiRssiDbm: Int? = null,

    @get:PropertyName("wifi_signal_pct") @set:PropertyName("wifi_signal_pct") @field:PropertyName("wifi_signal_pct")
    var wifiSignalPct: Int? = null,

    @get:PropertyName("wifi_ssid") @set:PropertyName("wifi_ssid") @field:PropertyName("wifi_ssid")
    var wifiSsid: String? = null,

    @get:PropertyName("wifi_strength") @set:PropertyName("wifi_strength") @field:PropertyName("wifi_strength")
    var wifiStrength: String? = null,
    
    @get:PropertyName("connection_status") @set:PropertyName("connection_status") @field:PropertyName("connection_status")
    var connectionStatus: ConnectionStatusDto? = null,
    
    @get:PropertyName("available_networks") @set:PropertyName("available_networks") @field:PropertyName("available_networks")
    var availableNetworks: Map<String, WifiNetworkDto>? = null,
    
    @get:PropertyName("scan_meta") @set:PropertyName("scan_meta") @field:PropertyName("scan_meta")
    var scanMeta: ScanMetaDto? = null,
    
    @get:PropertyName("last_updated") @set:PropertyName("last_updated") @field:PropertyName("last_updated")
    var lastUpdated: Long? = null
)

data class ConnectionStatusDto(
    @get:PropertyName("current_ssid") @set:PropertyName("current_ssid") @field:PropertyName("current_ssid")
    var currentSsid: String? = null,
    
    @get:PropertyName("wifi_status") @set:PropertyName("wifi_status") @field:PropertyName("wifi_status")
    var wifiStatus: String? = null,
    
    @get:PropertyName("firebase_status") @set:PropertyName("firebase_status") @field:PropertyName("firebase_status")
    var firebaseStatus: String? = null,
    
    @get:PropertyName("ip_address") @set:PropertyName("ip_address") @field:PropertyName("ip_address")
    var ipAddress: String? = null,
    
    @get:PropertyName("last_sync") @set:PropertyName("last_sync") @field:PropertyName("last_sync")
    var lastSync: Long? = null
)

data class WifiNetworkDto(
    var ssid: String? = null,
    var rssi: Int? = null,
    var security: String? = null,
    
    @get:PropertyName("signal_pct") @set:PropertyName("signal_pct") @field:PropertyName("signal_pct")
    var signalPct: Int? = null,
    
    var strength: String? = null,
    var channel: Int? = null
)

data class ScanMetaDto(
    @get:PropertyName("scan_status") @set:PropertyName("scan_status") @field:PropertyName("scan_status")
    var scanStatus: String? = null,
    
    @get:PropertyName("scanned_at") @set:PropertyName("scanned_at") @field:PropertyName("scanned_at")
    var scannedAt: Long? = null,
    
    @get:PropertyName("total_found") @set:PropertyName("total_found") @field:PropertyName("total_found")
    var totalFound: Int? = null
)
