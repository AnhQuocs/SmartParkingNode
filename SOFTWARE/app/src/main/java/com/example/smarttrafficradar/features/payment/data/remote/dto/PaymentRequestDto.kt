package com.example.smarttrafficradar.features.payment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaymentRequestDto(
    @SerializedName("uid")
    val uid: String,
    @SerializedName("amount")
    val amount: Int
)