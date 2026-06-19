package com.example.smarttrafficradar.features.payment.data.repository

import com.example.smarttrafficradar.features.payment.data.remote.PaymentApi
import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentRequestDto
import com.example.smarttrafficradar.features.payment.domain.model.PaymentInfo
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: PaymentApi
) : PaymentRepository {
    override suspend fun createMomoUrl(uid: String, amount: Int): Result<PaymentInfo> {
        return try {
            val request = PaymentRequestDto(uid, amount)
            val response = api.createMomoUrl(request)

            if (response.isSuccessful) {
                val payUrl = response.body()?.payUrl
                if (!payUrl.isNullOrEmpty()) {
                    Result.success(PaymentInfo(payUrl = payUrl))
                } else {
                    Result.failure(Exception("The payment link came back empty"))
                }
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Server error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}