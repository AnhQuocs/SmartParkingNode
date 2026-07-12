package com.example.smarttrafficradar.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.features.user_profile.domain.usecase.UserProfileUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterCardState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val selectedVehicleType: VehicleType = VehicleType.MOTORBIKE,
    val isAlreadyRegistered: Boolean = false,
    val currentRfidUid: String? = null,
    val currentVehicleType: VehicleType? = null
)

@HiltViewModel
class RegisterCardViewModel @Inject constructor(
    private val userProfileUseCases: UserProfileUseCases,
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterCardState())
    val state = _state.asStateFlow()

    fun observeRegistrationStatus(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userProfileUseCases.getUserProfile(uid).collect { profile ->
                if (profile != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAlreadyRegistered = profile.isActive && !profile.rfidUid.isNullOrEmpty(),
                        currentRfidUid = profile.rfidUid,
                        currentVehicleType = profile.vehicleType
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "User profile not found")
                }
            }
        }
    }

    fun onVehicleTypeSelected(vehicleType: VehicleType) {
        _state.value = _state.value.copy(selectedVehicleType = vehicleType)
    }

    fun sendRegistrationRequest(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Lấy thông tin profile tại thời điểm gửi
                val profile = userProfileUseCases.getUserProfile(uid).firstOrNull()
                
                if (profile != null) {
                    val request = RegistrationRequest(
                        uid = profile.uid,
                        fullName = profile.fullName,
                        identifier = profile.identifier,
                        vehicleType = _state.value.selectedVehicleType,
                        status = RegistrationStatus.PENDING,
                        timestamp = System.currentTimeMillis()
                    )
                    
                    registrationUseCases.sendRegistrationRequest(request)
                            
                    _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "User profile not found")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
