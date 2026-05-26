package com.example.smarttrafficradar.features.system_config.data.dto

import com.google.firebase.database.PropertyName

data class SystemConfigDto(
    @get:PropertyName("laser_enabled")
    @set:PropertyName("laser_enabled")
    @field:PropertyName("laser_enabled")
    var laserEnabled: Boolean? = null,

    @get:PropertyName("last_ping")
    @set:PropertyName("last_ping")
    @field:PropertyName("last_ping")
    var lastPing: Long? = null,

    @get:PropertyName("status")
    @set:PropertyName("status")
    @field:PropertyName("status")
    var status: String? = null,

    @get:PropertyName("v_max_threshold")
    @set:PropertyName("v_max_threshold")
    @field:PropertyName("v_max_threshold")
    var vMaxThreshold: Int? = null
)
