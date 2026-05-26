package com.example.smarttrafficradar.features.system_config.domain.usecase

import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import com.example.smarttrafficradar.features.system_config.domain.repository.SystemConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSystemConfigUseCase @Inject constructor(
    private val repository: SystemConfigRepository
) {
    operator fun invoke(nodeId: String): Flow<SystemConfig> {
        return repository.getSystemConfig(nodeId)
    }
}
