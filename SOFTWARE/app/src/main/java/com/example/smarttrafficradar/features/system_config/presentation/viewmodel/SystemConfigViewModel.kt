<<<<<<< HEAD
package com.example.smarttrafficradar.features.system_config.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import com.example.smarttrafficradar.features.system_config.domain.usecase.GetSystemConfigUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SystemConfigState {
    object Loading : SystemConfigState()
    data class Success(val config: SystemConfig) : SystemConfigState()
    data class Error(val message: UiText) : SystemConfigState()
}

@HiltViewModel
class SystemConfigViewModel @Inject constructor(
    private val getSystemConfigUseCase: GetSystemConfigUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SystemConfigState>(SystemConfigState.Loading)
    val state = _state.asStateFlow()

    init {
        loadSystemConfig()
    }

    fun loadSystemConfig(nodeId: String = "radar_node_01") {
        viewModelScope.launch {
            _state.value = SystemConfigState.Loading
            getSystemConfigUseCase(nodeId)
                .catch { e ->
                    _state.value = SystemConfigState.Error(
                        UiText.StringResource(R.string.error_unexpected)
                    )
                }
                .collect { config ->
                    _state.value = SystemConfigState.Success(config)
                }
        }
    }
}
=======
package com.example.smarttrafficradar.features.system_config.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import com.example.smarttrafficradar.features.system_config.domain.usecase.GetSystemConfigUseCase
import com.example.smarttrafficradar.features.system_config.domain.usecase.UpdateVMaxThresholdUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SystemConfigState {
    object Loading : SystemConfigState()
    data class Success(val config: SystemConfig) : SystemConfigState()
    data class Error(val message: UiText) : SystemConfigState()
}

@HiltViewModel
class SystemConfigViewModel @Inject constructor(
    private val getSystemConfigUseCase: GetSystemConfigUseCase,
    private val updateVMaxThresholdUseCase: UpdateVMaxThresholdUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<SystemConfigState>(SystemConfigState.Loading)
    val state = _state.asStateFlow()

    init {
        loadSystemConfig()
    }

    fun loadSystemConfig(nodeId: String = "radar_node_01") {
        viewModelScope.launch {
            _state.value = SystemConfigState.Loading
            getSystemConfigUseCase(nodeId)
                .catch { e ->
                    _state.value = SystemConfigState.Error(
                        UiText.StringResource(R.string.error_unexpected)
                    )
                }
                .collect { config ->
                    _state.value = SystemConfigState.Success(config)
                }
        }
    }

    fun updateVMaxThreshold(threshold: Int, nodeId: String = "radar_node_01") {
        viewModelScope.launch {
            try {
                updateVMaxThresholdUseCase(nodeId, threshold)
            } catch (e: Exception) {
                // Optionally handle error
            }
        }
    }
}
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
