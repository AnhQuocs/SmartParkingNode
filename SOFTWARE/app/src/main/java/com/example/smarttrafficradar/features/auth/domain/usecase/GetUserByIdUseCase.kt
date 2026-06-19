package com.example.smarttrafficradar.features.auth.domain.usecase

import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(userId: String): Flow<AuthUser?> {
        if (userId.isBlank()) {
            throw AuthError.EmptyUserId()
        }
        return repository.getUserById(userId)
    }
}