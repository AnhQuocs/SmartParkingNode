package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

fun RegistrationRequestDto.toDomain() = RegistrationRequest(
    id = id.orEmpty(),
    uid = uid.orEmpty(),
    fullName = fullName.orEmpty(),
    identifier = identifier.orEmpty(),
    status = status?.let { RegistrationStatus.valueOf(it) } ?: RegistrationStatus.PENDING,
    timestamp = timestamp ?: 0L,
    vehicleType = vehicleType?.let { VehicleType.valueOf(it) } ?: VehicleType.MOTORBIKE
)

fun RegistrationRequest.toDto() = RegistrationRequestDto(
    id = id,
    uid = uid,
    fullName = fullName,
    identifier = identifier,
    status = status.name,
    timestamp = timestamp,
    vehicleType = vehicleType.name
)
