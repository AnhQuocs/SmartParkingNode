package com.example.smarttrafficradar.features.system_config.domain.model

data class SystemConfig(
    val nodeId: String,
    val laserEnabled: Boolean,
    val lastPing: Long,
    val status: String,
    val vMaxThreshold: Int,
    val isOnline: Boolean
)
