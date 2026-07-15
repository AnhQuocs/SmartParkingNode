package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ManagementRegisterCardState(
    val isScanning: Boolean = false,
    val scannedUid: String? = null,
    val scanTimeLeft: Int = 30,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ManagementRegisterCardViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ManagementRegisterCardState())
    val state = _state.asStateFlow()

    private val parkingStatusRef = database.getReference("parking_status")
    private var scanTimerJob: Job? = null
    private var scanListener: ValueEventListener? = null

    fun startScanning() {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "current_mode" to 1,
                    "cloud_command/cmd" to "REGISTER_MODE"
                )
                parkingStatusRef.updateChildren(updates).await()
                
                _state.value = _state.value.copy(isScanning = true, scannedUid = null, scanTimeLeft = 30)
                
                observeScannedUid()
                startTimer()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun observeScannedUid() {
        scanListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val uid = snapshot.getValue(String::class.java)
                if (!uid.isNullOrEmpty() && _state.value.isScanning) {
                    _state.value = _state.value.copy(scannedUid = uid)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        parkingStatusRef.child("last_scanned_uid").addValueEventListener(scanListener!!)
    }

    private fun startTimer() {
        scanTimerJob?.cancel()
        scanTimerJob = viewModelScope.launch {
            while (_state.value.scanTimeLeft > 0 && _state.value.isScanning) {
                delay(1000)
                _state.value = _state.value.copy(scanTimeLeft = _state.value.scanTimeLeft - 1)
            }
            if (_state.value.isScanning) {
                stopScanning()
            }
        }
    }

    fun stopScanning() {
        viewModelScope.launch {
            try {
                val updates = mapOf(
                    "current_mode" to 0,
                    "cloud_command/cmd" to "IDLE"
                )
                parkingStatusRef.updateChildren(updates).await()
                
                _state.value = _state.value.copy(isScanning = false)
                removeScanListener()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun removeScanListener() {
        scanListener?.let {
            parkingStatusRef.child("last_scanned_uid").removeEventListener(it)
            scanListener = null
        }
    }

    fun approveRegistration(uid: String, rfidUid: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                // Update rfidUid in the registration request first
                database.getReference("registration_requests").child(uid).child("rfidUid").setValue(rfidUid).await()
                
                // Approve the registration
                registrationUseCases.approveRegistration(uid)
                
                // Clear last_scanned_uid in Firebase RTDB after successful registration
                parkingStatusRef.child("last_scanned_uid").setValue("").await()
                
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        removeScanListener()
    }
}
