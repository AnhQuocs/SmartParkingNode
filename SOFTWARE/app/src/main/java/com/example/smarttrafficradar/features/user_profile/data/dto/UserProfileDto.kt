package com.example.smarttrafficradar.features.user_profile.data.dto

import com.google.firebase.firestore.PropertyName

data class UserProfileDto(
    var uid: String = "",
    var identifier: String = "",
    var fullName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var memberType: String = "STUDENT",
    var department: String = "",
    var avatarUrl: String? = null,
    var rfidUid: String? = null,
    var currentDebt: Int = 0,
    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = false,
    @get:PropertyName("isParking")
    @set:PropertyName("isParking")
    var isParking: Boolean = false,
    var vehicleType: String? = null,
    var createdAt: Long = 0,
    var updatedAt: Long = 0
)