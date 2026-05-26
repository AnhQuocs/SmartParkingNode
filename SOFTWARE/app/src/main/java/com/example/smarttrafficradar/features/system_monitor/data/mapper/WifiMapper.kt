package com.example.smarttrafficradar.features.system_monitor.data.mapper

import com.example.smarttrafficradar.features.system_monitor.data.dto.WifiConfigDto
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiStatus

fun WifiConfigDto.toDomain(): WifiConfig {
    return WifiConfig(
        ssid = ssid ?: "",
        status = when (status?.uppercase()) {
            "SUCCESS" -> WifiStatus.SUCCESS
            "FAILED" -> WifiStatus.FAILED
            "PENDING" -> WifiStatus.PENDING
            else -> WifiStatus.IDLE
        },
        message = message
    )
}
