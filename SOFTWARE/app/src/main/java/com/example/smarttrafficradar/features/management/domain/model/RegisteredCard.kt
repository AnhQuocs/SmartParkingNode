package com.example.smarttrafficradar.features.management.domain.model

import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

enum class CardStatus {
    ACTIVE,
    BLOCKED
}

data class RegisteredCard(
    val id: String = "",
    val userId: String = "",
    val rfidUid: String = "",
    val ownerName: String = "",
    val identifier: String = "",
    val department: String = "",
    val cardType: MemberType = MemberType.STUDENT,
    val vehicleType: VehicleType = VehicleType.MOTORBIKE,
    val status: CardStatus = CardStatus.ACTIVE,
    val registeredAt: Long = 0L,
    val lastUsedAt: Long = 0L
)
