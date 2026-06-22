package com.example.smarttrafficradar.features.auth.data.api

import com.example.smarttrafficradar.features.auth.data.dto.FcmTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("/api/user/fcm-token")
    suspend fun registerFcmToken(
        @Body request: FcmTokenRequestDto
    ): Response<String>
}