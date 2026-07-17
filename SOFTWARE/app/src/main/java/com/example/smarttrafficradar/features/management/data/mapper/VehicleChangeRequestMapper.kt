package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.VehicleChangeRequestDto
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

fun VehicleChangeRequestDto.toDomain() = VehicleChangeRequest(
    uid = uid.orEmpty(),
    fullName = fullName.orEmpty(),
    identifier = identifier.orEmpty(),
    rfidUid = rfidUid.orEmpty(),
    currentVehicleType = currentVehicleType?.let {
        try {
            VehicleType.valueOf(it)
        } catch (e: Exception) {
            VehicleType.MOTORBIKE
        }
    } ?: VehicleType.MOTORBIKE,
    requestedVehicleType = requestedVehicleType?.let {
        try {
            VehicleType.valueOf(it)
        } catch (e: Exception) {
            VehicleType.MOTORBIKE
        }
    } ?: VehicleType.MOTORBIKE,
    memberType = memberType?.let {
        try {
            MemberType.valueOf(it)
        } catch (e: Exception) {
            MemberType.STUDENT
        }
    } ?: MemberType.STUDENT,
    timestamp = timestamp ?: 0L,
    status = status?.let {
        try {
            RegistrationStatus.valueOf(it)
        } catch (e: Exception) {
            RegistrationStatus.PENDING
        }
    } ?: RegistrationStatus.PENDING
)

fun VehicleChangeRequest.toDto() = VehicleChangeRequestDto(
    uid = uid,
    fullName = fullName,
    identifier = identifier,
    rfidUid = rfidUid,
    currentVehicleType = currentVehicleType.name,
    requestedVehicleType = requestedVehicleType.name,
    memberType = memberType.name,
    timestamp = timestamp,
    status = status.name
)
