package com.example.smarttrafficradar.features.system_monitor.data.dto

import com.google.firebase.database.PropertyName

data class WifiConfigDto(
    @get:PropertyName("ssid")
    @set:PropertyName("ssid")
    @field:PropertyName("ssid")
    var ssid: String? = null,

    @get:PropertyName("password")
    @set:PropertyName("password")
    @field:PropertyName("password")
    var password: String? = null,

    @get:PropertyName("status")
    @set:PropertyName("status")
    @field:PropertyName("status")
    var status: String? = null,

    @get:PropertyName("message")
    @set:PropertyName("message")
    @field:PropertyName("message")
    var message: String? = null
)
