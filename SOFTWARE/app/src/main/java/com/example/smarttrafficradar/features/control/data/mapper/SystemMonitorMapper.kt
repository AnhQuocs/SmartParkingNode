package com.example.smarttrafficradar.features.control.data.mapper

import com.example.smarttrafficradar.features.control.data.dto.ConnectionStatusDto
import com.example.smarttrafficradar.features.control.data.dto.ParkingNodeDto
import com.example.smarttrafficradar.features.control.data.dto.SystemMonitorDto
import com.example.smarttrafficradar.features.control.domain.model.ConnectionStatus
import com.example.smarttrafficradar.features.control.domain.model.ParkingNode
import com.example.smarttrafficradar.features.control.domain.model.SystemMonitor

fun SystemMonitorDto.toDomain(): SystemMonitor {
    return SystemMonitor(
        parkingNode = parkingNode?.toDomain() ?: ParkingNode()
    )
}

fun ParkingNodeDto.toDomain(): ParkingNode {
    return ParkingNode(
        connectionStatus = connectionStatus?.toDomain() ?: ConnectionStatus(),
        cpuTempC = cpuTempC ?: 0.0,
        cpuUsagePct = cpuUsagePct ?: 0,
        deviceId = deviceId ?: "",
        heapUsagePct = heapUsagePct ?: 0.0,
        irInStatus = irInStatus ?: "",
        irOutStatus = irOutStatus ?: "",
        powerStatus = powerStatus ?: "",
        rfidStatus = rfidStatus ?: "",
        wifiRssiDbm = wifiRssiDbm ?: 0,
        wifiSignalPct = wifiSignalPct ?: 0
    )
}

fun ConnectionStatusDto.toDomain(): ConnectionStatus {
    return ConnectionStatus(
        firebaseStatus = firebaseStatus ?: "",
        ipAddress = ipAddress ?: "",
        wifiStatus = wifiStatus ?: ""
    )
}
