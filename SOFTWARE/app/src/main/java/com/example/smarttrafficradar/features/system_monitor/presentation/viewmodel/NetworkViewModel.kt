package com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiNetwork
import com.example.smarttrafficradar.features.system_monitor.domain.usecase.ObserveWifiConfigUseCase
import com.example.smarttrafficradar.features.system_monitor.domain.usecase.ScanWifiUseCase
import com.example.smarttrafficradar.features.system_monitor.domain.usecase.UpdateWifiConfigUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NetworkState {
    object Idle : NetworkState()
    object Scanning : NetworkState()
    data class Success(val networks: List<WifiNetwork>) : NetworkState()
    data class Error(val message: UiText) : NetworkState()
}

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val scanWifiUseCase: ScanWifiUseCase,
    private val updateWifiConfigUseCase: UpdateWifiConfigUseCase,
    private val observeWifiConfigUseCase: ObserveWifiConfigUseCase
) : ViewModel() {

    private val _networkState = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val _wifiConfig = MutableStateFlow<WifiConfig?>(null)
    val wifiConfig: StateFlow<WifiConfig?> = _wifiConfig.asStateFlow()

    init {
        // Observe config by default for the status screen
        observeWifiStatus("radar_node_01")
    }

    fun startScanning() {
        _networkState.value = NetworkState.Scanning
        viewModelScope.launch {
            scanWifiUseCase()
                .catch { e ->
                    _networkState.value = NetworkState.Error(UiText.DynamicString(e.message ?: "Unknown error"))
                }
                .collect { networks ->
                    _networkState.value = NetworkState.Success(networks)
                }
        }
    }

    fun connectToWifi(ssid: String, password: String, nodeId: String = "radar_node_01") {
        viewModelScope.launch {
            try {
                updateWifiConfigUseCase(nodeId, ssid, password)
            } catch (e: Exception) {
                _networkState.value = NetworkState.Error(
                    UiText.DynamicString(e.message ?: "Failed to update config")
                )
            }
        }
    }

    private fun observeWifiStatus(nodeId: String) {
        viewModelScope.launch {
            observeWifiConfigUseCase(nodeId).collect { config ->
                _wifiConfig.value = config
            }
        }
    }
}
