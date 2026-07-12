package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegisteredCardsUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    operator fun invoke(): Flow<List<RegisteredCard>> {
        return repository.getRegisteredCards()
    }
}