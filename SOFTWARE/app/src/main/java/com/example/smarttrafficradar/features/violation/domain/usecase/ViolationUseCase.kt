package com.example.smarttrafficradar.features.violation.domain.usecase

import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.domain.repository.ViolationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveViolationListUseCase @Inject constructor(
    private val repository: ViolationRepository
) {
    operator fun invoke(nodeId: String): Flow<List<Violation>> {
        return repository.observeViolationList(nodeId)
    }
}