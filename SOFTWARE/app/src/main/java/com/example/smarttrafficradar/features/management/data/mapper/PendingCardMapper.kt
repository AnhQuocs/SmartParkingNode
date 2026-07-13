package com.example.smarttrafficradar.features.management.data.mapper

import com.example.smarttrafficradar.features.management.data.dto.PendingCardDto
import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.domain.model.PendingCardStatus

fun PendingCardDto.toDomain(): PendingCard {
    return PendingCard(
        uid = uid ?: "",
        fullName = fullName ?: "",
        identifier = identifier ?: "",
        reason = reason ?: "Unknown",
        timestamp = timestamp ?: 0L,
        status = try {
            PendingCardStatus.valueOf(status ?: PendingCardStatus.PENDING.name)
        } catch (e: Exception) {
            PendingCardStatus.PENDING
        }
    )
}
