package com.example.smarttrafficradar.features.daily_stats.domain.usecase

import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats
import com.example.smarttrafficradar.features.daily_stats.domain.repository.DailyStatsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyStatsUseCase @Inject constructor(
    private val repository: DailyStatsRepository
) {
    operator fun invoke(nodeId: String, date: String): Flow<DailyStats> {
        return repository.getDailyStatsFlow(nodeId, date)
    }
}