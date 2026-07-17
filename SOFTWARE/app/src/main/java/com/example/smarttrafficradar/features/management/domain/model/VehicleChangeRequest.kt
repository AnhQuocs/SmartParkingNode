package com.example.smarttrafficradar.features.management.domain.model

import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

data class VehicleChangeRequest(
    val uid: String = "",
    val fullName: String = "",
    val identifier: String = "",
    val rfidUid: String = "",
    val memberType: MemberType = MemberType.STUDENT,
    val currentVehicleType: VehicleType = VehicleType.MOTORBIKE,
    val requestedVehicleType: VehicleType = VehicleType.CAR,
    val timestamp: Long = 0L,
    val status: RegistrationStatus = RegistrationStatus.PENDING
)
