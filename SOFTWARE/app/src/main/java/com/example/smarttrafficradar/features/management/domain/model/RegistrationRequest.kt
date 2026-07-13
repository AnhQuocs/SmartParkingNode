package com.example.smarttrafficradar.features.management.domain.model

import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

enum class RegistrationStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class RegistrationRequest(
    val id: String = "",
    val uid: String = "",
    val rfidUid: String = "",
    val fullName: String = "",
    val identifier: String = "",
    val department: String = "",
    val cardType: MemberType = MemberType.STUDENT,
    val status: RegistrationStatus = RegistrationStatus.PENDING,
    val timestamp: Long = 0L,
    val vehicleType: VehicleType = VehicleType.MOTORBIKE
)
