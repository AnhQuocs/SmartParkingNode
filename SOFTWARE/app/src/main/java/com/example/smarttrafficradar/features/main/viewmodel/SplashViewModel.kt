package com.example.smarttrafficradar.features.main.viewmodel

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
                val role = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()
                    .getString("role") ?: "USER"

                if (role == "ADMIN") {
                    onResult("admin_root")
                } else {
                    onResult("user_root")
                }
            } catch (e: Exception) {
                onResult("user_root")
            }
        }
    }
}