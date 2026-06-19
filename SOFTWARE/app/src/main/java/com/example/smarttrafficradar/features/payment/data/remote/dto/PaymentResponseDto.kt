package com.example.smarttrafficradar.features.payment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentResponseDto(
    @SerializedName("payUrl")
    val payUrl: String?
)