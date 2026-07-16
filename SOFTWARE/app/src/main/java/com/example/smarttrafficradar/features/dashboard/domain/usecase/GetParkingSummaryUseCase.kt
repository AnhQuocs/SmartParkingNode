package com.example.smarttrafficradar.features.dashboard.domain.usecase

import com.example.smarttrafficradar.features.dashboard.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetParkingSummaryUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    operator fun invoke() = repository.getParkingSummary()
}
