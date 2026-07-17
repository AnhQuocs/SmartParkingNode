package com.example.smarttrafficradar.features.management.domain.usecase.cards

import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.domain.repository.PendingCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPendingCardsUseCase @Inject constructor(
    private val repository: PendingCardRepository
) {
    operator fun invoke(): Flow<List<PendingCard>> {
        return repository.getPendingCards()
    }
}
