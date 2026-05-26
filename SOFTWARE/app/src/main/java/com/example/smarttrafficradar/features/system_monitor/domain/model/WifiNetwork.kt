package com.example.smarttrafficradar.features.system_monitor.domain.model

data class WifiNetwork(
    val ssid: String,
    val signalLevel: Int, // 0 to 4
    val isSecure: Boolean
)
