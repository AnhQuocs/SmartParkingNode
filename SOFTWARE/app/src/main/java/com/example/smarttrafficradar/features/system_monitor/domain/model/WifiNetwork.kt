package com.example.smarttrafficradar.features.system_monitor.domain.model

data class WifiNetwork(
    val ssid: String,
    val rssi: Int,
    val security: String,
    val signalPct: Int,
    val strength: String,
    val channel: Int
)
