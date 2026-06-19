package com.example.smarttrafficradar.features.history.data.mapper

import com.example.smarttrafficradar.features.history.data.dto.ParkingHistoryDto
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingStatus
import com.google.firebase.Timestamp

fun ParkingHistoryDto.toDomain() = ParkingHistory(
    id = id.orEmpty(),
    userId = userId.orEmpty(),
    rfidUid = rfidUid.orEmpty(),
    checkInTime = checkInTime.toMillis(),
    checkOutTime = checkOutTime?.toDate()?.time,
    durationMinutes = durationMinutes,
    fee = fee,
    status = runCatching {
        ParkingStatus.valueOf(status)
    }.getOrDefault(ParkingStatus.CHECK_IN),
    createdAt = createdAt.toMillis(),
    updatedAt = updatedAt.toMillis()
)

fun ParkingHistory.toDto() = ParkingHistoryDto(
    id = id,
    userId = userId,
    rfidUid = rfidUid,
    checkInTime = checkInTime.toTimestamp(),
    checkOutTime = checkOutTime?.toTimestamp(),
    durationMinutes = durationMinutes,
    fee = fee,
    status = status.name,
    createdAt = createdAt.toTimestamp(),
    updatedAt = updatedAt.toTimestamp()
)

fun Timestamp?.toMillis(): Long {
    return this?.toDate()?.time ?: 0L
}

fun Long.toTimestamp(): Timestamp {
    return Timestamp(java.util.Date(this))
}