package com.example.smarttrafficradar.features.user_profile.data.dto

data class UserProfileDto(
    var uid: String = "",
    var identifier: String = "",
    var fullName: String = "",
    var phoneNumber: String = "",
    var memberType: String = "STUDENT",
    var department: String = "",
    var avatarUrl: String? = null,
    var rfidUid: String? = null,
    var currentDebt: Long = 0,
    var isActive: Boolean = false,
    var vehicleType: String? = null,
    var createdAt: Long = 0,
    var updatedAt: Long = 0
)
