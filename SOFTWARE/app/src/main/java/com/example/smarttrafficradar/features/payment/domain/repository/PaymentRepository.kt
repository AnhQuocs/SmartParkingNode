package com.example.smarttrafficradar.features.payment.domain.repository

import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.features.payment.domain.model.PaymentInfo
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun createMomoUrl(uid: String, amount: Int): Result<PaymentInfo>
    fun getPaymentHistories(userId: String): Flow<List<PaymentHistory>>
    fun getAllTransactions(): Flow<List<PaymentHistory>>
    suspend fun saveTransaction(transaction: PaymentHistory): Result<Unit>
}
