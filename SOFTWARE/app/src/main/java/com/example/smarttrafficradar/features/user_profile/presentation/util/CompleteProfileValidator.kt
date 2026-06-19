package com.example.smarttrafficradar.features.user_profile.presentation.util

object CompleteProfileValidator {
    private val phoneRegex = Regex("^0\\d{9}$")

    fun validatePhoneNumber(phone: String): Boolean {
        return phone.trim().matches(phoneRegex)
    }
}