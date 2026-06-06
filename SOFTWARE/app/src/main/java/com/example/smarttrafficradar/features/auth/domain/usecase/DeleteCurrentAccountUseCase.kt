package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteCurrentAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        try {
            repository.deleteCurrentAccount()
        } catch (e: Exception) {
            throw AuthError.RemoteError(e.message)
        }
    }
}