package com.example.smarttrafficradar.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
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
    val memberType: MemberType = MemberType.STUDENT,
    val isAlreadyRegistered: Boolean = false,
    val isLocked: Boolean = false,
    val currentRfidUid: String? = null,
    val currentVehicleType: VehicleType? = null,
    val currentFullName: String? = null,
    val currentIdentifier: String? = null,
    val showChangeLockView: Boolean = false,
    val isVehicleChangeSuccess: Boolean = false,
    val isLockCardSuccess: Boolean = false
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
                    val hasRfid = !profile.rfidUid.isNullOrEmpty()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isAlreadyRegistered = profile.isActive && hasRfid,
                        isLocked = !profile.isActive && hasRfid,
                        currentRfidUid = profile.rfidUid,
                        currentVehicleType = profile.vehicleType,
                        currentFullName = profile.fullName,
                        currentIdentifier = profile.identifier,
                        memberType = profile.memberType
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

    fun setShowChangeLockView(show: Boolean) {
        _state.value = _state.value.copy(showChangeLockView = show)
    }

    fun sendRegistrationRequest(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
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

    fun sendVehicleChangeRequest(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val currentState = _state.value
                val newVehicleType = if (currentState.currentVehicleType == VehicleType.CAR) VehicleType.MOTORBIKE else VehicleType.CAR
                
                val request = VehicleChangeRequest(
                    uid = uid,
                    fullName = currentState.currentFullName ?: "",
                    identifier = currentState.currentIdentifier ?: "",
                    rfidUid = currentState.currentRfidUid ?: "",
                    currentVehicleType = currentState.currentVehicleType ?: VehicleType.MOTORBIKE,
                    requestedVehicleType = newVehicleType,
                    memberType = currentState.memberType,
                    timestamp = System.currentTimeMillis(),
                    status = RegistrationStatus.PENDING
                )
                
                registrationUseCases.sendVehicleChangeRequest(request)
                _state.value = _state.value.copy(isLoading = false, isVehicleChangeSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun lockCard(uid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val cardId = _state.value.currentIdentifier ?: ""
                registrationUseCases.lockCard(uid, cardId)
                _state.value = _state.value.copy(isLoading = false, isLockCardSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun resetSuccess() {
        _state.value = _state.value.copy(
            isSuccess = false, 
            isVehicleChangeSuccess = false, 
            isLockCardSuccess = false,
            showChangeLockView = false
        )
    }
}
