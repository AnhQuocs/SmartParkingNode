package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import javax.inject.Inject

class SendVehicleChangeRequestUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(request: VehicleChangeRequest) {
        repository.sendVehicleChangeRequest(request)
    }
}
