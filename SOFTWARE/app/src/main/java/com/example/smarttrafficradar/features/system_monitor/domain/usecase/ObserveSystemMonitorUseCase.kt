package com.example.smarttrafficradar.features.system_monitor.domain.usecase

import com.example.smarttrafficradar.features.system_monitor.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSystemMonitorUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(nodeId: String): Flow<SystemMonitor> = repository.getSystemMonitor(nodeId)
}