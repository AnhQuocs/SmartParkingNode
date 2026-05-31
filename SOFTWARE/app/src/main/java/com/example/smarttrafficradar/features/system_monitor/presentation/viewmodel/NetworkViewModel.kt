package com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.system_monitor.data.preference.WifiPreferenceManager
import com.example.smarttrafficradar.features.system_monitor.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.usecase.ObserveSystemMonitorUseCase
import com.example.smarttrafficradar.features.system_monitor.domain.usecase.ObserveWifiConfigUseCase
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
    object Loading : NetworkState()
    data class Success(val monitor: SystemMonitor) : NetworkState()
    data class Error(val message: UiText) : NetworkState()
}

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val observeSystemMonitorUseCase: ObserveSystemMonitorUseCase,
    private val updateWifiConfigUseCase: UpdateWifiConfigUseCase,
    private val observeWifiConfigUseCase: ObserveWifiConfigUseCase,
    private val wifiPreferenceManager: WifiPreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val state: StateFlow<NetworkState> = _state.asStateFlow()

    private val _wifiConfig = MutableStateFlow<WifiConfig?>(null)
    val wifiConfig: StateFlow<WifiConfig?> = _wifiConfig.asStateFlow()

    private val nodeId = "radar_node_01"

    init {
        observeSystemMonitor()
        observeWifiStatus()
    }

    private fun observeSystemMonitor() {
        viewModelScope.launch {
            observeSystemMonitorUseCase(nodeId)
                .catch { e ->
                    _state.value =
                        NetworkState.Error(UiText.DynamicString(e.message ?: "Unknown error"))
                }
                .collect { monitor ->
                    _state.value = NetworkState.Success(monitor)
                }
        }
    }

    private fun observeWifiStatus() {
        viewModelScope.launch {
            observeWifiConfigUseCase(nodeId).collect { config ->
                _wifiConfig.value = config
            }
        }
    }

    fun handleWifiSelection(ssid: String, onRequirePassword: () -> Unit) {
        viewModelScope.launch {
            val currentSsid =
                (state.value as? NetworkState.Success)?.monitor?.connectionStatus?.currentSsid
            if (ssid == currentSsid) return@launch

            val savedPassword = wifiPreferenceManager.getWifiPassword(ssid)
            if (savedPassword != null) {
                connectToWifi(ssid, savedPassword)
            } else {
                onRequirePassword()
            }
        }
    }

    fun connectToWifi(ssid: String, password: String) {
        viewModelScope.launch {
            try {
                wifiPreferenceManager.saveWifiPassword(ssid, password)
                updateWifiConfigUseCase(nodeId, ssid, password)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun forgetWifi(ssid: String) {
        viewModelScope.launch {
            wifiPreferenceManager.forgetWifi(ssid)
        }
    }
}
