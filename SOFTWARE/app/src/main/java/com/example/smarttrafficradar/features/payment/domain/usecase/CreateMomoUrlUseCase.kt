package com.example.smarttrafficradar.features.payment.domain.usecase

import com.example.smarttrafficradar.features.payment.domain.model.PaymentInfo
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import javax.inject.Inject

class CreateMomoUrlUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(uid: String, amount: Int): Result<PaymentInfo> {
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Invalid amount"))
        }
        return repository.createMomoUrl(uid, amount)
    }
}