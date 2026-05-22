<<<<<<< HEAD
package com.example.smarttrafficradar.features.violation.domain.repository

import com.example.smarttrafficradar.features.violation.domain.model.Violation
import kotlinx.coroutines.flow.Flow

interface ViolationRepository {

    fun observeViolationList(nodeId: String): Flow<List<Violation>>
=======
package com.example.smarttrafficradar.features.violation.domain.repository

import com.example.smarttrafficradar.features.violation.domain.model.Violation
import kotlinx.coroutines.flow.Flow

interface ViolationRepository {

    fun observeViolationList(nodeId: String): Flow<List<Violation>>
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}