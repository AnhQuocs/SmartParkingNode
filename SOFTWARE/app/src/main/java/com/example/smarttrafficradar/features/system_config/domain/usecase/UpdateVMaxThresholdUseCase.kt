package com.example.smarttrafficradar.features.system_config.domain.usecase

import com.example.smarttrafficradar.features.system_config.domain.repository.SystemConfigRepository
import javax.inject.Inject

class UpdateVMaxThresholdUseCase @Inject constructor(
    private val repository: SystemConfigRepository
) {
    suspend operator fun invoke(nodeId: String, threshold: Int) {
        repository.updateVMaxThreshold(nodeId, threshold)
    }
}
