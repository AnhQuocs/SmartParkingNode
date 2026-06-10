package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateDebtUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(uid: String, amount: Long): Result<Unit> = runCatching {
        repository.updateDebt(uid, amount)
    }
}
