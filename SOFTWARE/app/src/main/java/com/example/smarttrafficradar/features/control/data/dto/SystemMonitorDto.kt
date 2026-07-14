package com.example.smarttrafficradar.features.control.data.dto

import com.google.firebase.database.PropertyName

data class SystemMonitorDto(
    @get:PropertyName("parking_node")
    @set:PropertyName("parking_node")
    var parkingNode: ParkingNodeDto? = null
)

data class ParkingNodeDto(
    @get:PropertyName("connection_status")
    @set:PropertyName("connection_status")
    var connectionStatus: ConnectionStatusDto? = null,
    
    @get:PropertyName("cpu_temp_c")
    @set:PropertyName("cpu_temp_c")
    var cpuTempC: Double? = null,
    
    @get:PropertyName("cpu_usage_pct")
    @set:PropertyName("cpu_usage_pct")
    var cpuUsagePct: Int? = null,
    
    @get:PropertyName("device_id")
    @set:PropertyName("device_id")
    var deviceId: String? = null,
    
    @get:PropertyName("heap_usage_pct")
    @set:PropertyName("heap_usage_pct")
    var heapUsagePct: Double? = null,
    
    @get:PropertyName("ir_in_status")
    @set:PropertyName("ir_in_status")
    var irInStatus: String? = null,
    
    @get:PropertyName("ir_out_status")
    @set:PropertyName("ir_out_status")
    var irOutStatus: String? = null,
    
    @get:PropertyName("power_status")
    @set:PropertyName("power_status")
    var powerStatus: String? = null,
    
    @get:PropertyName("rfid_status")
    @set:PropertyName("rfid_status")
    var rfidStatus: String? = null,
    
    @get:PropertyName("wifi_rssi_dbm")
    @set:PropertyName("wifi_rssi_dbm")
    var wifiRssiDbm: Int? = null,
    
    @get:PropertyName("wifi_signal_pct")
    @set:PropertyName("wifi_signal_pct")
    var wifiSignalPct: Int? = null
)

data class ConnectionStatusDto(
    @get:PropertyName("firebase_status")
    @set:PropertyName("firebase_status")
    var firebaseStatus: String? = null,
    
    @get:PropertyName("ip_address")
    @set:PropertyName("ip_address")
    var ipAddress: String? = null,
    
    @get:PropertyName("wifi_status")
    @set:PropertyName("wifi_status")
    var wifiStatus: String? = null
)
