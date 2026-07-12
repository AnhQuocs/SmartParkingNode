package com.example.smarttrafficradar.features.management.domain.model

import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

enum class CardStatus {
    ACTIVE,
    BLOCK
}

data class RegisteredCard(
    val id: String = "",
    val uid: String = "",
    val rfidUid: String = "",
    val vehicleType: VehicleType = VehicleType.MOTORBIKE,
    val status: CardStatus = CardStatus.ACTIVE,
    val ownerName: String = ""
)