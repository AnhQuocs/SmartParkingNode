package com.example.smarttrafficradar.features.history.domain.model

enum class ParkingStatus {
    CHECK_IN, CHECK_OUT
}

data class ParkingHistory(
    val id: String = "",
    val userId: String = "",
    val rfidUid: String = "",
    val checkInTime: Long = 0L,
    val checkOutTime: Long? = null,
    val durationMinutes: Long = 0L,
    val fee: Int = 0,
    val status: ParkingStatus = ParkingStatus.CHECK_IN,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)