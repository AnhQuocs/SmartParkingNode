package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class RejectRegistrationUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(uid: String) {
        repository.updateRegistrationStatus(uid, RegistrationStatus.REJECTED)
    }
}