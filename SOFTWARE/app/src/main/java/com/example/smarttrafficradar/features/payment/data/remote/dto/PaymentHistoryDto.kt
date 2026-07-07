package com.example.smarttrafficradar.features.payment.data.remote.dto

import com.google.firebase.Timestamp

data class PaymentHistoryDto(
    val id: String? = null,
    val userId: String? = null,
    val amount: Int = 0,
    val method: String = "MOMO",
    val status: String = "SUCCESS",
    val createdAt: Timestamp? = null
)