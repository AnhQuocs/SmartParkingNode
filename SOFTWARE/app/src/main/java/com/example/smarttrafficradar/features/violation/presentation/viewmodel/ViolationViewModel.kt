package com.example.smarttrafficradar.features.violation.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.domain.usecase.ObserveViolationListUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ViolationState {
    object Loading : ViolationState()
    data class Success(val violations: List<Violation>) : ViolationState()
    data class Error(val message: UiText) : ViolationState()
}

@HiltViewModel
class ViolationViewModel @Inject constructor(
    private val observeViolationListUseCase: ObserveViolationListUseCase
) : ViewModel() {

    private val _violationState = MutableStateFlow<ViolationState>(ViolationState.Loading)
    val violationState = _violationState.asStateFlow()

    init {
        loadViolationList()
    }

    private fun loadViolationList() {
        val nodeId = "radar_node_01"

        viewModelScope.launch {
            _violationState.value = ViolationState.Loading

            observeViolationListUseCase(nodeId)
                .catch { e ->
                    _violationState.value = ViolationState.Error(
                        UiText.StringResource(R.string.error_unexpected)
                    )
                }
                .collect { list ->
                    _violationState.value = ViolationState.Success(list)
                }
        }
    }
}