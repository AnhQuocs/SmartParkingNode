package com.example.smarttrafficradar.features.dashboard.domain.repository

import com.example.smarttrafficradar.features.dashboard.domain.model.ParkingSummary
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getParkingSummary(): Flow<ParkingSummary>
}
