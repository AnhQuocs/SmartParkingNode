package com.example.smarttrafficradar.features.management.domain.usecase.vehicle

import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class HandleVehicleChangeDecisionUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(uid: String, status: RegistrationStatus) {
        repository.handleVehicleChangeDecision(uid, status)
    }
}
