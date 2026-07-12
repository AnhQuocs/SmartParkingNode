package com.example.smarttrafficradar.features.management.domain.repository

import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    // Registration Request actions
    suspend fun sendRegistrationRequest(request: RegistrationRequest)
    fun getRegistrationRequests(): Flow<List<RegistrationRequest>>
    suspend fun handleRegistrationDecision(uid: String, status: RegistrationStatus)
    suspend fun updateRegistrationStatus(uid: String, status: RegistrationStatus)

    // Registered Card actions
    fun getRegisteredCards(): Flow<List<RegisteredCard>>
    suspend fun updateCardStatus(cardId: String, status: CardStatus)
}