package com.example.smarttrafficradar.features.payment.data.repository

import com.example.smarttrafficradar.features.payment.data.mapper.toDomain
import com.example.smarttrafficradar.features.payment.data.remote.PaymentApi
import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentHistoryDto
import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentRequestDto
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.features.payment.domain.model.PaymentInfo
import com.example.smarttrafficradar.features.payment.domain.repository.PaymentRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val api: PaymentApi,
    private val firestore: FirebaseFirestore
) : PaymentRepository {

    private val paymentCollection = firestore.collection("payment_histories")

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

    override fun getPaymentHistories(userId: String): Flow<List<PaymentHistory>> = callbackFlow {
        val subscription = paymentCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Thay vì close(error) gây crash, ta chỉ cần log và emit danh sách trống
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val histories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PaymentHistoryDto::class.java)
                        ?.copy(id = doc.id)
                        ?.toDomain()
                } ?: emptyList()
                trySend(histories)
            }
        awaitClose { subscription.remove() }
    }

    override fun getAllTransactions(): Flow<List<PaymentHistory>> = callbackFlow {
        val subscription = paymentCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val histories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(PaymentHistoryDto::class.java)
                        ?.copy(id = doc.id)
                        ?.toDomain()
                } ?: emptyList()
                trySend(histories)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveTransaction(transaction: PaymentHistory): Result<Unit> {
        return try {
            val dto = PaymentHistoryDto(
                userId = transaction.userId,
                amount = transaction.amount,
                method = transaction.method,
                status = transaction.status,
                createdAt = Timestamp(transaction.createdAt / 1000, ((transaction.createdAt % 1000) * 1000000).toInt())
            )
            paymentCollection.add(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
