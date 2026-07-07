package com.example.smarttrafficradar.features.history.data.mapper

import com.example.smarttrafficradar.features.history.data.dto.ParkingHistoryDto
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp

fun ParkingHistoryDto.toDomain() = ParkingHistory(
    id = id.orEmpty(),
    userId = userId.orEmpty(),
    rfidUid = rfidUid.orEmpty(),
    checkInTime = checkInTime.toMillis(),
    checkOutTime = checkOutTime?.toDate()?.time,
    durationMinutes = durationMinutes,
    vehicleType = vehicleType?.let {
        try {
            VehicleType.valueOf(it)
        } catch (e: Exception) {
            null
        }
    },
    notified30Min = notified30Min,
    notifiedNights = notifiedNights,
    fee = fee,
    status = runCatching {
        ParkingStatus.valueOf(status)
    }.getOrDefault(ParkingStatus.CHECK_IN),
    createdAt = createdAt.toMillis(),
    updatedAt = updatedAt.toMillis()
)

fun Timestamp?.toMillis(): Long {
    return this?.toDate()?.time ?: 0L
}