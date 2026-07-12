package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegistrationListState {
    data object Loading : RegistrationListState()
    data class Success(val requests: List<RegistrationRequest>) : RegistrationListState()
    data class Error(val message: String) : RegistrationListState()
}

@HiltViewModel
class RegistrationListViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<RegistrationListState>(RegistrationListState.Loading)
    val state = _state.asStateFlow()

    init {
        getRegistrationRequests()
    }

    private fun getRegistrationRequests() {
        viewModelScope.launch {
            registrationUseCases.getRegistrationRequests()
                .onStart {
                    _state.value = RegistrationListState.Loading
                }
                .catch { e ->
                    _state.value = RegistrationListState.Error(
                        e.message ?: "An unknown error occurred"
                    )
                }
                .collect { requests ->
                    _state.value = RegistrationListState.Success(
                        requests.sortedByDescending { it.timestamp }
                    )
                }
        }
    }

    fun approveRequest(uid: String) {
        viewModelScope.launch {
            try {
                registrationUseCases.approveRegistration(uid)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun rejectRequest(uid: String) {
        viewModelScope.launch {
            try {
                registrationUseCases.rejectRegistration(uid)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}