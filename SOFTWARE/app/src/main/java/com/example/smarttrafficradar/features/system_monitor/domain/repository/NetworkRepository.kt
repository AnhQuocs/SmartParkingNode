package com.example.smarttrafficradar.features.system_monitor.domain.repository

import com.example.smarttrafficradar.features.system_monitor.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getSystemMonitor(nodeId: String): Flow<SystemMonitor>
    suspend fun updateWifiConfig(nodeId: String, ssid: String, password: String)
    fun observeWifiConfig(nodeId: String): Flow<WifiConfig>
}
