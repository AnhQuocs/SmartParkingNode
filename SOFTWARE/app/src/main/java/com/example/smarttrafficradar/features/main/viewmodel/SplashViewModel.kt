package com.example.smarttrafficradar.features.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.onboarding.data.local.OnboardingDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore
) : ViewModel() {

    fun decideStartDestination(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val isOnboardingDone = onboardingDataStore.isOnboardingDone()
            val user = FirebaseAuth.getInstance().currentUser

            if (!isOnboardingDone) {
                onResult("onboarding_root")
                return@launch
            }

            if (user == null) {
                onResult("auth")
                return@launch
            }

            try {
                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                val role = document.getString("role") ?: "USER"
                val status = document.getString("status") ?: "PROFILE_INCOMPLETE"

                Log.d("SplashViewModel", "Role: $role")
                Log.d("SplashViewModel", "Status: $status")

                if (role == "ADMIN") {
                    onResult("admin_root")
                } else {
                    if (status == "PROFILE_INCOMPLETE") {
                        onResult("profile_completion_root")
                    } else {
                        onResult("user_root")
                    }
                }
            } catch (e: Exception) {
                onResult("auth")
            }
        }
    }
}
