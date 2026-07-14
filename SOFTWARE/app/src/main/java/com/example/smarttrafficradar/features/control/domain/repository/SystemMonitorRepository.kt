package com.example.smarttrafficradar.features.control.domain.repository

import com.example.smarttrafficradar.features.control.domain.model.SystemMonitor
import kotlinx.coroutines.flow.Flow

interface SystemMonitorRepository {
    fun getSystemMonitor(): Flow<SystemMonitor>
}
