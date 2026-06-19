package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        try {
            repository.signOut()
        } catch (e: Exception) {
            throw AuthError.RemoteError(e.message)
        }
    }
}