package com.example.smarttrafficradar.features.violation.domain.model

data class Violation(
    val nodeId: String,
    val id: String,
    val resolved: Boolean,
    val speedKmh: Double,
    val timestamp: Long,
    val vehicleId: String
)