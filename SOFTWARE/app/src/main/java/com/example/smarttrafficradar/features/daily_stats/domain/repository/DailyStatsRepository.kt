package com.example.smarttrafficradar.features.daily_stats.domain.repository

import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats
import kotlinx.coroutines.flow.Flow

interface DailyStatsRepository {
    fun getDailyStatsFlow(nodeId: String, date: String): Flow<DailyStats>
}