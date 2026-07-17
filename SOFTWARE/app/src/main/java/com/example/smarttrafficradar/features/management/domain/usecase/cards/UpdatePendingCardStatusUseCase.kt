package com.example.smarttrafficradar.features.management.domain.usecase.cards

import com.example.smarttrafficradar.features.management.domain.model.PendingCardStatus
import com.example.smarttrafficradar.features.management.domain.repository.PendingCardRepository
import javax.inject.Inject

class UpdatePendingCardStatusUseCase @Inject constructor(
    private val repository: PendingCardRepository
) {
    suspend operator fun invoke(uid: String, status: PendingCardStatus) {
        repository.updatePendingCardStatus(uid, status)
    }
}
