package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpAdminUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        adminCode: String
    ): AuthUser {
        if (username.isBlank()) {
            throw AuthError.EmptyUsername()
        }
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw AuthError.InvalidEmail()
        }
        if (password.length < 6) {
            throw AuthError.PasswordTooShort()
        }
        if (adminCode.isBlank()) {
            throw AuthError.EmptyAdminCode()
        }

        return try {
            repository.signUpAdmin(username, email, password, adminCode)
        } catch (e: Exception) {
            throw AuthError.RemoteError(e.message)
        }
    }
}