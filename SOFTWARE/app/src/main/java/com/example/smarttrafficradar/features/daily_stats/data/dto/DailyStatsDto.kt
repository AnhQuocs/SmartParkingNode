package com.example.smarttrafficradar.features.daily_stats.data.dto

import com.google.firebase.database.PropertyName

data class DailyStatsDto(
    @get:PropertyName("average_speed")
    @set:PropertyName("average_speed")
    @field:PropertyName("average_speed")
    var averageSpeed: Double? = null,

    @get:PropertyName("peak_hour")
    @set:PropertyName("peak_hour")
    @field:PropertyName("peak_hour")
    var peakHour: String? = null,

    @get:PropertyName("total_vehicles")
    @set:PropertyName("total_vehicles")
    @field:PropertyName("total_vehicles")
    var totalVehicles: Int? = null,

    @get:PropertyName("total_violations")
    @set:PropertyName("total_violations")
    @field:PropertyName("total_violations")
    var totalViolations: Int? = null
)