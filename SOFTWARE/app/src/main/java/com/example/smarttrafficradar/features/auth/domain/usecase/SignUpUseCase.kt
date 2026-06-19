package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
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
        
        return try {
            repository.signUp(username, email, password)
        } catch (e: Exception) {
            throw AuthError.RemoteError(e.message)
        }
    }
}