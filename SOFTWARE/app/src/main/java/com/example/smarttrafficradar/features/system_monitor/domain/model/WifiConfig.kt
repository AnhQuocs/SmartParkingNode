package com.example.smarttrafficradar.features.system_monitor.domain.model

data class WifiConfig(
    val ssid: String,
    val status: WifiStatus = WifiStatus.IDLE,
    val message: String? = null
)

enum class WifiStatus {
    IDLE, PENDING, SUCCESS, FAILED
}
