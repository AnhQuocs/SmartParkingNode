package com.example.smarttrafficradar.features.auth.data.mapper

import android.R.attr.phoneNumber
import com.example.smarttrafficradar.features.auth.data.dto.AuthUserDto
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.model.UserRole
import com.example.smarttrafficradar.features.auth.domain.model.UserStatus
import kotlinx.coroutines.NonCancellable.isActive

fun AuthUserDto.toDomain(): AuthUser {
    return AuthUser(
        uid = uid ?: "",
        email = email,
        avatar = avatar,
        username = username,
        role = if(role == "ADMIN") UserRole.ADMIN else UserRole.USER,
        status = status?.let { UserStatus.valueOf(it) },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun AuthUser.toDto(): AuthUserDto {
    return AuthUserDto(
        uid = uid,
        email = email,
        avatar = avatar,
        username = username,
        role = role.name,
        status = status?.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}