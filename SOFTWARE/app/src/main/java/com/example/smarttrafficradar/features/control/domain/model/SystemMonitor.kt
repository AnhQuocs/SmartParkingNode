package com.example.smarttrafficradar.features.control.domain.model

data class SystemMonitor(
    val parkingNode: ParkingNode = ParkingNode()
)

data class ParkingNode(
    val connectionStatus: ConnectionStatus = ConnectionStatus(),
    val cpuTempC: Double = 0.0,
    val cpuUsagePct: Int = 0,
    val deviceId: String = "",
    val heapUsagePct: Double = 0.0,
    val irInStatus: String = "",
    val irOutStatus: String = "",
    val powerStatus: String = "",
    val rfidStatus: String = "",
    val wifiRssiDbm: Int = 0,
    val wifiSignalPct: Int = 0
)

data class ConnectionStatus(
    val firebaseStatus: String = "",
    val ipAddress: String = "",
    val wifiStatus: String = ""
)
