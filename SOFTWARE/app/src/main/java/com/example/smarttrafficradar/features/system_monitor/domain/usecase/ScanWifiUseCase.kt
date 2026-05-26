package com.example.smarttrafficradar.features.system_monitor.domain.usecase

import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiNetwork
import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanWifiUseCase @Inject constructor(
    private val repository: NetworkRepository
) {
    operator fun invoke(): Flow<List<WifiNetwork>> = repository.scanWifiNetworks()
}
