package com.example.smarttrafficradar.features.history.data.dto

import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp

data class ParkingHistoryDto(
    val id: String? = null,
    val userId: String? = null,
    val rfidUid: String? = null,
    val checkInTime: Timestamp? = null,
    val checkOutTime: Timestamp? = null,
    val durationMinutes: Int = 0,
    val vehicleType: String? = null,
    val fee: Int = 0,
    val status: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)