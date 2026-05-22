package com.example.smarttrafficradar.features.violation.data.dto

import com.google.firebase.database.PropertyName

data class ViolationDto(
    var resolved: Boolean? = null,

    @get: PropertyName("speed_kmh")
    @set: PropertyName("speed_kmh")
    @field: PropertyName("speed_kmh")
    var speedKmh: Double? = null,

    var timestamp: Long? = null,

    @get: PropertyName("v_max")
    @set: PropertyName("v_max")
    @field: PropertyName("v_max")
    var vMax: Int? = null,

    @get: PropertyName("vehicle_id")
    @set: PropertyName("vehicle_id")
    @field: PropertyName("vehicle_id")
    var vehicleId: String? = null
)
