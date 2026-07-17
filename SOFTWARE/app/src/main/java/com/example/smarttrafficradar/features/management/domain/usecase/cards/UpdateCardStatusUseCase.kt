package com.example.smarttrafficradar.features.management.domain.usecase.cards

import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class UpdateCardStatusUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(cardId: String, status: CardStatus) {
        repository.updateCardStatus(cardId, status)
    }
}
