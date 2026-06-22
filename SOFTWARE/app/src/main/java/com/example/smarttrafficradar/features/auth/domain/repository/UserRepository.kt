package com.example.smarttrafficradar.features.auth.domain.repository

interface UserRepository {
    suspend fun updateFcmToken(uid: String, fcmToken: String): Result<Unit>
}