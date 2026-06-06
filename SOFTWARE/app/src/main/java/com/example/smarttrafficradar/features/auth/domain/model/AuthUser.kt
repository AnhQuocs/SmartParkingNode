package com.example.smarttrafficradar.features.auth.domain.model

import com.google.firebase.Timestamp

enum class UserRole {
    USER,
    ADMIN
}

enum class UserStatus {
    PROFILE_INCOMPLETE,
    ACTIVE,
    BLOCKED
}

data class AuthUser(
    val uid: String,
    val email: String? = null,
    val username: String? = null,
    val avatar: String? = null,
    val role: UserRole = UserRole.USER,
    val status: UserStatus? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)