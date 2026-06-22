package com.example.smarttrafficradar.features.auth.data.repository

import com.example.smarttrafficradar.features.auth.data.api.UserApi
import com.example.smarttrafficradar.features.auth.data.dto.FcmTokenRequestDto
import com.example.smarttrafficradar.features.auth.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
) : UserRepository {
    override suspend fun updateFcmToken(uid: String, fcmToken: String): Result<Unit> {
        return try {
            val response = api.registerFcmToken(FcmTokenRequestDto(uid, fcmToken))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}