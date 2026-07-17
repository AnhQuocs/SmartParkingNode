package com.example.smarttrafficradar.features.dashboard.domain.model

data class ParkingAnalytics(
    val todayInCount: Int = 0,
    val todayOutCount: Int = 0,
    val todayRevenue: Long = 0,
    val vehiclesInLot: Int = 0,
    val vehicleTypes: Map<String, Int> = emptyMap()
)

data class ParkingSummary(
    val current: ParkingAnalytics = ParkingAnalytics(),
    val revenueChangePercentage: Double = 0.0
)
