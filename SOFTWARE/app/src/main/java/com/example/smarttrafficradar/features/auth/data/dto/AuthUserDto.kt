package com.example.smarttrafficradar.features.auth.data.dto

import com.google.firebase.Timestamp

class AuthUserDto(
    val uid: String? = "",
    val email: String? = "",
    val avatar: String? = null,
    val username: String? = "",
    val role: String? = "USER",
    val status: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)