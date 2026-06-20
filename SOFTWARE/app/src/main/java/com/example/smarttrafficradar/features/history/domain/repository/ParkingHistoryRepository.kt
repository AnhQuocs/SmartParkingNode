package com.example.smarttrafficradar.features.history.domain.repository

import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import kotlinx.coroutines.flow.Flow

interface ParkingHistoryRepository {
    fun observeHistoriesByUserId(userId: String): Flow<List<ParkingHistory>>
    suspend fun getHistoryDetail(historyId: String): ParkingHistory
}