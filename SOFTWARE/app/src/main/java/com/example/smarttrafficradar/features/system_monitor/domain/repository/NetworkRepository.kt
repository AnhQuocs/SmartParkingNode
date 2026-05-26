package com.example.smarttrafficradar.features.system_monitor.domain.repository

import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun scanWifiNetworks(): Flow<List<WifiNetwork>>
    suspend fun updateWifiConfig(nodeId: String, ssid: String, password: String)
    fun observeWifiConfig(nodeId: String): Flow<WifiConfig>
}
