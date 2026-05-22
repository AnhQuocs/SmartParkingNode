package com.example.smarttrafficradar.features.violation.domain.repository

import com.example.smarttrafficradar.features.violation.domain.model.Violation
import kotlinx.coroutines.flow.Flow

interface ViolationRepository {

    fun observeViolationList(nodeId: String): Flow<List<Violation>>
}