package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class VehicleChangeRequestsState {
    data object Loading : VehicleChangeRequestsState()
    data class Success(val requests: List<VehicleChangeRequest>) : VehicleChangeRequestsState()
    data class Error(val message: String) : VehicleChangeRequestsState()
}

@HiltViewModel
class VehicleChangeRequestsViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<VehicleChangeRequestsState>(VehicleChangeRequestsState.Loading)
    val state = _state.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    init {
        getVehicleChangeRequests()
    }

    private fun getVehicleChangeRequests() {
        viewModelScope.launch {
            registrationUseCases.getVehicleChangeRequests()
                .onStart {
                    _state.value = VehicleChangeRequestsState.Loading
                }
                .catch { e ->
                    _state.value = VehicleChangeRequestsState.Error(
                        e.message ?: "An unknown error occurred"
                    )
                }
                .collect { requests ->
                    _state.value = VehicleChangeRequestsState.Success(
                        requests = requests.sortedByDescending { it.timestamp }
                    )
                }
        }
    }

    fun approveRequest(uid: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                registrationUseCases.handleVehicleChangeDecision(uid, RegistrationStatus.APPROVED)
            } catch (e: Exception) {
                _state.value = VehicleChangeRequestsState.Error(e.message ?: "Failed to approve request")
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun rejectRequest(uid: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            try {
                registrationUseCases.handleVehicleChangeDecision(uid, RegistrationStatus.REJECTED)
            } catch (e: Exception) {
                _state.value = VehicleChangeRequestsState.Error(e.message ?: "Failed to reject request")
            } finally {
                _isProcessing.value = false
            }
        }
    }
}
