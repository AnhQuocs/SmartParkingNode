package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class SendRegistrationRequestUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(request: RegistrationRequest) {
        repository.sendRegistrationRequest(request)
    }
}