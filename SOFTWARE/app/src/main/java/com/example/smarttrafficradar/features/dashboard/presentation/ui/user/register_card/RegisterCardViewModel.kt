package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.features.user_profile.domain.usecase.UserProfileUseCases
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private val db: FirebaseDatabase
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
                userProfileUseCases.getUserProfile(uid).collect { profile ->
                    if (profile != null) {
                        val request = mapOf(
                            "uid" to profile.uid,
                            "fullName" to profile.fullName,
                            "identifier" to profile.identifier,
                            "vehicleType" to _state.value.selectedVehicleType.name,
                            "status" to "PENDING",
                            "timestamp" to System.currentTimeMillis()
                        )
                        
                        db.getReference("registration_requests")
                            .child(uid)
                            .setValue(request)
                            .await()
                            
                        _state.value = _state.value.copy(isLoading = false, isSuccess = true)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
