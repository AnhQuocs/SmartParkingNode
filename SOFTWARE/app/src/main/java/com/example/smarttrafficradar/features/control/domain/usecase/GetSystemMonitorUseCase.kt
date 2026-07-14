package com.example.smarttrafficradar.features.control.domain.usecase

import com.example.smarttrafficradar.features.control.domain.repository.SystemMonitorRepository
import javax.inject.Inject

class GetSystemMonitorUseCase @Inject constructor(
    private val repository: SystemMonitorRepository
) {
    operator fun invoke() = repository.getSystemMonitor()
}
