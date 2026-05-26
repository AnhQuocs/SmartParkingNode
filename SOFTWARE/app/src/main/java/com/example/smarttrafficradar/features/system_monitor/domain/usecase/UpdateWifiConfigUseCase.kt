package com.example.smarttrafficradar.features.system_monitor.domain.usecase

import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import javax.inject.Inject

class UpdateWifiConfigUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    suspend operator fun invoke(nodeId: String, ssid: String, password: String) {
        repository.updateWifiConfig(nodeId, ssid, password)
    }
}
