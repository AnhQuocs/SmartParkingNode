<<<<<<< HEAD
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
=======
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
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}