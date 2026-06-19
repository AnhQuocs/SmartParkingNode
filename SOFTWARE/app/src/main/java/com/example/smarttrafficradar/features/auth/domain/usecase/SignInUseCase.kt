package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthUser {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw AuthError.InvalidEmail()
        }
        if (password.isBlank()) {
            throw AuthError.EmptyPassword()
        }
        
        return try {
            repository.signIn(email, password)
        } catch (e: Exception) {
            throw AuthError.RemoteError(e.message)
        }
    }
}