package com.example.smarttrafficradar.features.history.data.repository

import com.example.smarttrafficradar.features.history.data.dto.ParkingHistoryDto
import com.example.smarttrafficradar.features.history.data.mapper.toDomain
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.repository.ParkingHistoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class ParkingHistoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ParkingHistoryRepository {

    private val collection = firestore.collection("parking_histories")

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val historyFlowCache = ConcurrentHashMap<String, Flow<List<ParkingHistory>>>()

    override fun observeHistoriesByUserId(userId: String): Flow<List<ParkingHistory>> {
        // Trả về Flow rỗng ngay lập tức nếu userId không hợp lệ
        if (userId.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }

        return historyFlowCache.getOrPut(userId) {
            callbackFlow {
                val subscription = collection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error) // Báo lỗi cho Flow để phía ViewModel có thể catch được
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
            }.shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000L), // Giữ kết nối thêm 5s sau khi không còn ai observe
                replay = 1 // Cache lại list history mới nhất trên RAM
            )
        }
    }

    override suspend fun getHistoryDetail(historyId: String): ParkingHistory {
        val document = collection.document(historyId).get().await()

        return document.toObject(ParkingHistoryDto::class.java)
            ?.copy(id = document.id)
            ?.toDomain()
            ?: throw Exception("History not found!")
    }
}