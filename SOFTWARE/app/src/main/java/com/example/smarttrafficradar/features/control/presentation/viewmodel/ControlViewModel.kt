package com.example.smarttrafficradar.features.control.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.control.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.control.domain.usecase.GetSystemMonitorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(
    getSystemMonitorUseCase: GetSystemMonitorUseCase
) : ViewModel() {

    val systemMonitorState: StateFlow<SystemMonitor> = getSystemMonitorUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SystemMonitor()
        )
}
