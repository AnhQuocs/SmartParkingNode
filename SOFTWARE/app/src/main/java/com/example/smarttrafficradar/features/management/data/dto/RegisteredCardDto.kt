package com.example.smarttrafficradar.features.management.data.dto

import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

data class RegisteredCardDto(
    val id: String = "",
    val uid: String = "",
    val rfidUid: String = "",
    val vehicleType: String = VehicleType.MOTORBIKE.name,
    val status: String = CardStatus.ACTIVE.name,
    val ownerName: String = ""
)