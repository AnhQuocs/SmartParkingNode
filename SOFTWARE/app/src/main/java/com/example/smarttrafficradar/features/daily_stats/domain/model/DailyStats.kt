package com.example.smarttrafficradar.features.daily_stats.domain.model

data class DailyStats(
    val nodeId: String,
    val date: String,
    val averageSpeed: Double,
    val peakHour: String,
    val totalVehicles: Int,
    val totalViolations: Int
)