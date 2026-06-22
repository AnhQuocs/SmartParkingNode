package com.example.smarttrafficradar.features.auth.data.dto

import com.google.gson.annotations.SerializedName

data class FcmTokenRequestDto(
    @SerializedName("uid")
    val uid: String,
    @SerializedName("fcmToken")
    val fcmToken: String
)