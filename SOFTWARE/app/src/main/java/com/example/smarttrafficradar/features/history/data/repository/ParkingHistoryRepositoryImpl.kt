package com.example.smarttrafficradar.features.history.data.repository

import com.example.smarttrafficradar.features.history.data.dto.ParkingHistoryDto
import com.example.smarttrafficradar.features.history.data.mapper.toDomain
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.repository.ParkingHistoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ParkingHistoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ParkingHistoryRepository {

    private val collection = firestore.collection("parking_histories")

    override fun observeHistoriesByUserId(userId: String): Flow<List<ParkingHistory>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = collection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val histories = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ParkingHistoryDto::class.java)
                        ?.copy(id = doc.id)
                        ?.toDomain()
                } ?: emptyList()

                trySend(histories)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun getHistoryDetail(historyId: String): ParkingHistory {
        return try {
            val document = collection.document(historyId).get().await()
            document.toObject(ParkingHistory::class.java)?.copy(id = document.id)
                ?: throw Exception("History not found!")
        } catch (e: Exception) {
            throw e
        }
    }
}