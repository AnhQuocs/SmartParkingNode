package com.example.smarttrafficradar.features.system_monitor.domain.model

data class SystemMonitor(
    val deviceId: String,
    val deviceType: String,
    val firmware: String,
    val uptime: String,
    val cpuUsagePct: Int,
    val cpuTempC: Double,
    val cycleTimeMs: Int,
    val dbStatus: String,
    val fps: Double,
    val heapFreeBytes: Long,
    val heapUsagePct: Double,
    val ir1Status: String,
    val ir2Status: String,
    val pingFirebaseMs: Int,
    val reconnectCount: Int,
    val rfidStatus: String,
    val wifiRssiDbm: Int,
    val wifiSignalPct: Int,
    val wifiSsid: String,
    val wifiStrength: String,
    val connectionStatus: ConnectionStatus,
    val availableNetworks: List<WifiNetwork>,
    val scanMeta: ScanMeta,
    val lastUpdated: Long
)

data class ConnectionStatus(
    val currentSsid: String,
    val wifiStatus: String,
    val firebaseStatus: String,
    val ipAddress: String,
    val lastSync: Long
)

data class ScanMeta(
    val scanStatus: String,
    val scannedAt: Long,
    val totalFound: Int
)
