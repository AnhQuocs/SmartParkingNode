package com.example.smarttrafficradar.features.payment.domain.repository

import com.example.smarttrafficradar.features.payment.domain.model.PaymentInfo

interface PaymentRepository {
    suspend fun createMomoUrl(uid: String, amount: Int): Result<PaymentInfo>
}