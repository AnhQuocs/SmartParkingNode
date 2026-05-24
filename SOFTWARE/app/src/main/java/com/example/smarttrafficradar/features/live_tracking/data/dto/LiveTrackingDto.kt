package com.example.smarttrafficradar.features.live_tracking.data.dto

import com.google.firebase.database.PropertyName

data class VehicleDto(
    @get:PropertyName("is_violation")
    @set:PropertyName("is_violation")
    @field:PropertyName("is_violation")
    var isViolation: Boolean? = null,

    @get:PropertyName("speed_kmh")
    @set:PropertyName("speed_kmh")
    @field:PropertyName("speed_kmh")
    var speedKmh: Double? = null,

    var timestamp: Long? = null,

    @get:PropertyName("vehicle_id")
    @set:PropertyName("vehicle_id")
    @field:PropertyName("vehicle_id")
    var vehicleId: String? = null
)

data class LiveTrackingDto(
    @get:PropertyName("latest_vehicle")
    @set:PropertyName("latest_vehicle")
    @field:PropertyName("latest_vehicle")
    var latestVehicle: VehicleDto? = null,

    @get:PropertyName("recent_vehicles")
    @set:PropertyName("recent_vehicles")
    @field:PropertyName("recent_vehicles")
    var recentVehicles: Map<String, VehicleDto>? = null
)