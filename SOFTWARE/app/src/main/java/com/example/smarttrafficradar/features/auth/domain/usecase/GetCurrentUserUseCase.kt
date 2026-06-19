package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<AuthUser?> {
        return repository.getCurrentUser()
    }
}