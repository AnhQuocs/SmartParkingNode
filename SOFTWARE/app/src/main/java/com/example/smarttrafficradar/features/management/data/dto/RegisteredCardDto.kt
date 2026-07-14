package com.example.smarttrafficradar.features.management.data.dto

import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp

data class RegisteredCardDto(
    val id: String = "",
    val userId: String = "",
    val rfidUid: String = "",
    val ownerName: String = "",
    val identifier: String = "",
    val department: String = "",
    val cardType: String = MemberType.STUDENT.name,
    val vehicleType: String = VehicleType.MOTORBIKE.name,
    val status: String = CardStatus.ACTIVE.name,
    val registeredAt: Timestamp? = null,
    val lastUsedAt: Timestamp? = null
)
