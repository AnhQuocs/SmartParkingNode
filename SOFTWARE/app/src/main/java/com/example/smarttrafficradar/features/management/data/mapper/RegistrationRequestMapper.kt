package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp
import java.util.Date

fun RegistrationRequestDto.toDomain() = RegistrationRequest(
    id = id.orEmpty(),
    uid = uid.orEmpty(),
    fullName = fullName.orEmpty(),
    identifier = identifier.orEmpty(),
    status = status ?: RegistrationStatus.PENDING,
    timestamp = timestamp.toMillis(),
    vehicleType = vehicleType ?: VehicleType.MOTORBIKE
)

fun RegistrationRequest.toDto() = RegistrationRequestDto(
    id = id,
    uid = uid,
    fullName = fullName,
    identifier = identifier,
    status = status,
    timestamp = timestamp.toTimestamp(),
    vehicleType = vehicleType
)

fun Timestamp?.toMillis(): Long {
    return if (this == null) {
        0L
    } else {
        seconds * 1000 + nanoseconds / 1_000_000
    }
}

fun Long.toTimestamp(): Timestamp {
    return Timestamp(Date(this))
}