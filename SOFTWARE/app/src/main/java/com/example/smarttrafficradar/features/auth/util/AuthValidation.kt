package com.example.smarttrafficradar.features.auth.util

object AuthValidation {
    fun validateUsername(username: String): Boolean {
        return username.length >= 8
    }

    fun validateEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }
}