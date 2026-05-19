package com.example.smarttrafficradar.features.system_config.data.mapper

import com.example.smarttrafficradar.features.system_config.data.dto.SystemConfigDto
import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig

fun SystemConfigDto.toDomain(nodeId: String): SystemConfig {
    val lastPingTime = lastPing ?: 0L
    val currentTime = System.currentTimeMillis()
    val isOnline = (currentTime - lastPingTime) < 30_000 // 30 seconds

    return SystemConfig(
        nodeId = nodeId,
        laserEnabled = laserEnabled ?: false,
        lastPing = lastPingTime,
        status = status ?: "unknown",
        vMaxThreshold = vMaxThreshold ?: 0,
        isOnline = isOnline
    )
}
