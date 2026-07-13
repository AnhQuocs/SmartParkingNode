package com.example.smarttrafficradar.features.management.domain.repository

import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.domain.model.PendingCardStatus
import kotlinx.coroutines.flow.Flow

interface PendingCardRepository {
    fun getPendingCards(): Flow<List<PendingCard>>
    suspend fun updatePendingCardStatus(uid: String, status: PendingCardStatus)
}
