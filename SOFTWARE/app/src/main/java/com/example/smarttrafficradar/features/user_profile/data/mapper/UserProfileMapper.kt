package com.example.smarttrafficradar.features.user_profile.data.mapper

import com.example.smarttrafficradar.features.user_profile.data.dto.UserProfileDto
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType

fun UserProfileDto.toDomain(): UserProfile {
    return UserProfile(
        uid = uid,
        identifier = identifier,
        fullName = fullName,
        phoneNumber = phoneNumber,
        memberType = try { MemberType.valueOf(memberType) } catch (e: Exception) { MemberType.STUDENT },
        department = department,
        avatarUrl = avatarUrl,
        rfidUid = rfidUid,
        currentDebt = currentDebt,
        isActive = isActive,
        vehicleType = vehicleType?.let { try { VehicleType.valueOf(it) } catch (e: Exception) { null } },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun UserProfile.toDto(): UserProfileDto {
    return UserProfileDto(
        uid = uid,
        identifier = identifier,
        fullName = fullName,
        phoneNumber = phoneNumber,
        memberType = memberType.name,
        department = department,
        avatarUrl = avatarUrl,
        rfidUid = rfidUid,
        currentDebt = currentDebt,
        isActive = isActive,
        vehicleType = vehicleType?.name,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
