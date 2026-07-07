package com.example.smarttrafficradar.features.payment.domain.model

data class PaymentHistory(
    val id: String = "",
    val userId: String = "",
    val amount: Int = 0,
    val method: String = "MOMO",
    val status: String = "SUCCESS",
    val createdAt: Long = System.currentTimeMillis()
)
