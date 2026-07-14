package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.RegisteredCardDto
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp
import java.util.Date

fun RegisteredCardDto.toDomain() = RegisteredCard(
    id = id,
    userId = userId,
    rfidUid = rfidUid,
    ownerName = ownerName,
    identifier = identifier,
    department = department,
    cardType = try { MemberType.valueOf(cardType) } catch (e: Exception) { MemberType.STUDENT },
    vehicleType = try { VehicleType.valueOf(vehicleType) } catch (e: Exception) { VehicleType.MOTORBIKE },
    status = try { CardStatus.valueOf(status) } catch (e: Exception) { CardStatus.ACTIVE },
    registeredAt = registeredAt?.toDate()?.time ?: 0L,
    lastUsedAt = lastUsedAt?.toDate()?.time ?: 0L
)

fun RegisteredCard.toDto() = RegisteredCardDto(
    id = id,
    userId = userId,
    rfidUid = rfidUid,
    ownerName = ownerName,
    identifier = identifier,
    department = department,
    cardType = cardType.name,
    vehicleType = vehicleType.name,
    status = status.name,
    registeredAt = if (registeredAt != 0L) Timestamp(Date(registeredAt)) else null,
    lastUsedAt = if (lastUsedAt != 0L) Timestamp(Date(lastUsedAt)) else null
)
