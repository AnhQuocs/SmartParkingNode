package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

fun RegistrationRequestDto.toDomain() = RegistrationRequest(
    id = id.orEmpty(),
    uid = uid.orEmpty(),
    rfidUid = rfidUid.orEmpty(),
    fullName = fullName.orEmpty(),
    identifier = identifier.orEmpty(),
    department = department.orEmpty(),
    cardType = try { MemberType.valueOf(cardType ?: "") } catch (e: Exception) { MemberType.STUDENT },
    status = status?.let { try { RegistrationStatus.valueOf(it) } catch (e: Exception) { RegistrationStatus.PENDING } } ?: RegistrationStatus.PENDING,
    timestamp = timestamp ?: 0L,
    vehicleType = vehicleType?.let { try { VehicleType.valueOf(it) } catch (e: Exception) { VehicleType.MOTORBIKE } } ?: VehicleType.MOTORBIKE
)

fun RegistrationRequest.toDto() = RegistrationRequestDto(
    id = id,
    uid = uid,
    rfidUid = rfidUid,
    fullName = fullName,
    identifier = identifier,
    department = department,
    cardType = cardType.name,
    status = status.name,
    timestamp = timestamp,
    vehicleType = vehicleType.name
)
