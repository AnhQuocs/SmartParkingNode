package com.example.smarttrafficradar.features.live_tracking.domain.repository

import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import kotlinx.coroutines.flow.Flow

interface LiveTrackingRepository {
    fun observeLiveTracking(nodeId: String): Flow<LiveTracking>
}