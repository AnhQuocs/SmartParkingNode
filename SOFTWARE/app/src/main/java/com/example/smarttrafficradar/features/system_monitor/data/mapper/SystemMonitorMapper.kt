package com.example.smarttrafficradar.features.system_monitor.data.mapper

import com.example.smarttrafficradar.features.system_monitor.data.dto.*
import com.example.smarttrafficradar.features.system_monitor.domain.model.*

fun SystemMonitorDto.toDomain(): SystemMonitor {
    return SystemMonitor(
        deviceId = deviceId ?: "",
        deviceType = deviceType ?: "",
        firmware = firmware ?: "",
        uptime = uptime ?: "",
        cpuUsagePct = cpuUsagePct ?: 0,
        cpuTempC = cpuTempC ?: 0.0,
        cycleTimeMs = cycleTimeMs ?: 0,
        dbStatus = dbStatus ?: "Unknown",
        fps = fps ?: 0.0,
        heapFreeBytes = heapFreeBytes ?: 0L,
        heapUsagePct = heapUsagePct ?: 0.0,
        ir1Status = ir1Status ?: "Unknown",
        ir2Status = ir2Status ?: "Unknown",
        pingFirebaseMs = pingFirebaseMs ?: 0,
        reconnectCount = reconnectCount ?: 0,
        rfidStatus = rfidStatus ?: "Unknown",
        wifiRssiDbm = wifiRssiDbm ?: 0,
        wifiSignalPct = wifiSignalPct ?: 0,
        wifiSsid = wifiSsid ?: "",
        wifiStrength = wifiStrength ?: "Unknown",
        connectionStatus = connectionStatus?.toDomain() ?: ConnectionStatus("", "", "", "", 0L),
        availableNetworks = availableNetworks?.map { it.value.toDomain() } ?: emptyList(),
        scanMeta = scanMeta?.toDomain() ?: ScanMeta("", 0L, 0),
        lastUpdated = lastUpdated ?: 0L
    )
}

fun ConnectionStatusDto.toDomain(): ConnectionStatus {
    return ConnectionStatus(
        currentSsid = currentSsid ?: "",
        wifiStatus = wifiStatus ?: "",
        firebaseStatus = firebaseStatus ?: "",
        ipAddress = ipAddress ?: "",
        lastSync = lastSync ?: 0L
    )
}

fun WifiNetworkDto.toDomain(): WifiNetwork {
    return WifiNetwork(
        ssid = ssid ?: "",
        rssi = rssi ?: 0,
        security = security ?: "Unknown",
        signalPct = signalPct ?: 0,
        strength = strength ?: "Unknown",
        channel = channel ?: 0
    )
}

fun ScanMetaDto.toDomain(): ScanMeta {
    return ScanMeta(
        scanStatus = scanStatus ?: "",
        scannedAt = scannedAt ?: 0L,
        totalFound = totalFound ?: 0
    )
}
