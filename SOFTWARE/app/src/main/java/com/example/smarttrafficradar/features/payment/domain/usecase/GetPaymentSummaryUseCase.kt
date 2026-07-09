package com.example.smarttrafficradar.features.payment.domain.usecase

import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class PaymentSummary(
    val totalPaid: Int = 0,
    val totalTransactions: Int = 0,
    val lastPaidAt: String = "---"
)

class GetPaymentSummaryUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    operator fun invoke(userId: String): Flow<PaymentSummary> {
        return repository.getPaymentHistories(userId).map { histories ->
            val total = histories.sumOf { it.amount }
            val count = histories.size
            val lastDate = histories.maxByOrNull { it.createdAt }?.let {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.format(Date(it.createdAt))
            } ?: "---"
            
            PaymentSummary(
                totalPaid = total,
                totalTransactions = count,
                lastPaidAt = lastDate
            )
        }
    }
}
