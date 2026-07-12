package com.example.smarttrafficradar.features.management.domain.repository

import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import kotlinx.coroutines.flow.Flow

interface RegistrationRepository {
    suspend fun sendRegistrationRequest(request: RegistrationRequest)
    fun getRegistrationRequests(): Flow<List<RegistrationRequest>>
    suspend fun updateRegistrationStatus(uid: String, status: RegistrationStatus)
}