package com.example.smarttrafficradar.features.user_profile.domain.model

enum class UserLang {
    EN, VI
}

data class UserProfile(
    val uid: String = "",
    val identifier: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val memberType: MemberType = MemberType.STUDENT,
    val department: String = "",
    val avatarUrl: String? = null,
    val rfidUid: String? = null,
    val currentDebt: Int = 0,
    val isActive: Boolean = false,
    val isParking: Boolean = false,
    val vehicleType: VehicleType? = null,
    val language: UserLang? = UserLang.VI,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class VehicleType {
    MOTORBIKE,
    CAR
}

enum class MemberType {
    STUDENT,
    EMPLOYEE
}
