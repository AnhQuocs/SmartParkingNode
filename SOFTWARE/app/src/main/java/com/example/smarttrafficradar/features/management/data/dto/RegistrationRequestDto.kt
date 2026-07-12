package com.example.smarttrafficradar.features.management.data.dto

import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.google.firebase.Timestamp

data class RegistrationRequestDto(
    val id: String? = null,
    val uid: String? = null,
    val fullName: String? = null,
    val identifier: String? = null,
    val status: RegistrationStatus? = null,
    val timestamp: Timestamp? = null,
    val vehicleType: VehicleType? = null
)