package com.example.smarttrafficradar.features.payment.domain.usecase

import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentHistoriesUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(userId: String): Flow<List<PaymentHistory>> {
        return repository.getPaymentHistories(userId)
    }
}
