<<<<<<< HEAD
package com.example.smarttrafficradar.features.system_config.domain.repository

import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow

interface SystemConfigRepository {
    fun getSystemConfig(nodeId: String): Flow<SystemConfig>
}
=======
package com.example.smarttrafficradar.features.system_config.domain.repository

import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow

interface SystemConfigRepository {
    fun getSystemConfig(nodeId: String): Flow<SystemConfig>
    suspend fun updateVMaxThreshold(nodeId: String, threshold: Int)
}
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
