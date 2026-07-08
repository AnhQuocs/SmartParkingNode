package com.example.smarttrafficradar.features.history.domain.model

import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

enum class ParkingStatus {
    CHECK_IN, CHECK_OUT
}

data class ParkingHistory(
    val id: String = "",
    val userId: String = "",
    val rfidUid: String = "",
    val checkInTime: Long = 0L,
    val checkOutTime: Long? = null,
    val durationMinutes: Int = 0,
    val notified30Min: Boolean? = false,
    val notifiedNights: Int? = 0,
    val vehicleType: VehicleType? = null,
    val fee: Int = 0,
    val status: ParkingStatus = ParkingStatus.CHECK_IN,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)