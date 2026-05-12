package com.example.smarttrafficradar.features.live_tracking.data.mapper

import com.example.smarttrafficradar.features.live_tracking.data.dto.LiveTrackingDto
import com.example.smarttrafficradar.features.live_tracking.domain.model.LastVehicle
import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import com.example.smarttrafficradar.features.live_tracking.domain.model.SpeedEntry

fun LiveTrackingDto.toDomain(nodeId: String): LiveTracking {
    val lastVehicle = latestVehicle?.let { dto ->
        LastVehicle(
            isViolation = dto.isViolation ?: false,
            speedKmh = dto.speedKmh ?: 0.0,
            timestamp = dto.timestamp ?: 0L,
            vehicleId = dto.vehicleId ?: "Unknown"
        )
    } ?: LastVehicle(false, 0.0, 0L, "No Data")

    val recentEntries = recentVehicles?.values
        ?.filter { it.timestamp != null }
        ?.sortedByDescending { it.timestamp }
        ?.take(10)
        ?.map {
            SpeedEntry(
                speed = it.speedKmh ?: 0.0,
                isViolation = it.isViolation ?: false
            )
        }
        ?.reversed()
        ?: emptyList()

    return LiveTracking(
        nodeId = nodeId,
        lastVehicle = lastVehicle,
        recentEntries = recentEntries
    )
}