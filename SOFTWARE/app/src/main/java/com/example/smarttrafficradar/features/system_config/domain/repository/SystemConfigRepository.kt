package com.example.smarttrafficradar.features.system_config.domain.repository

import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow

interface SystemConfigRepository {
    fun getSystemConfig(nodeId: String): Flow<SystemConfig>
}
