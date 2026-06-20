package com.example.smarttrafficradar.features.payment.data.remote

import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentRequestDto
import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PaymentApi {
    @POST("/api/payment/create-momo-url")
    suspend fun createMomoUrl(
        @Body request: PaymentRequestDto
    ): Response<PaymentResponseDto>
}