package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.RegisteredCardDto
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

fun RegisteredCardDto.toDomain() = RegisteredCard(
    id = id,
    uid = uid,
    rfidUid = rfidUid,
    vehicleType = try { VehicleType.valueOf(vehicleType) } catch (e: Exception) { VehicleType.MOTORBIKE },
    status = try { CardStatus.valueOf(status) } catch (e: Exception) { CardStatus.ACTIVE },
    ownerName = ownerName
)

fun RegisteredCard.toDto() = RegisteredCardDto(
    id = id,
    uid = uid,
    rfidUid = rfidUid,
    vehicleType = vehicleType.name,
    status = status.name,
    ownerName = ownerName
)