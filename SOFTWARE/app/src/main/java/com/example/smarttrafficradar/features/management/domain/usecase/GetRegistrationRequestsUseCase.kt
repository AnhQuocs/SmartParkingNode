package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegistrationRequestsUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    operator fun invoke(): Flow<List<RegistrationRequest>> {
        return repository.getRegistrationRequests()
    }
}