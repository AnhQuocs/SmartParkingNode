package com.example.smarttrafficradar.features.management.domain.usecase.cards

import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class LockCardUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(uid: String, cardId: String) {
        repository.lockCard(uid, cardId)
    }
}
