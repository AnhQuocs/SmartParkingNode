package com.example.smarttrafficradar.features.management.domain.usecase.vehicle

import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVehicleChangeRequestsUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    operator fun invoke(): Flow<List<VehicleChangeRequest>> {
        return repository.getVehicleChangeRequests()
    }
}
