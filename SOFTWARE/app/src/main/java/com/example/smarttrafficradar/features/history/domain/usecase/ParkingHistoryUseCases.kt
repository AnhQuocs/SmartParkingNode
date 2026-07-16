package com.example.smarttrafficradar.features.history.domain.usecase

import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistoryError
import com.example.smarttrafficradar.features.history.domain.repository.ParkingHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ParkingHistoryUseCases @Inject constructor(
    val observeHistoriesByUserIdUseCase: ObserveHistoriesByUserIdUseCase,
    val getHistoryDetailUseCase: GetHistoryDetailUseCase,
    val observeRecentHistoriesUseCase: ObserveRecentHistoriesUseCase
)

class ObserveHistoriesByUserIdUseCase @Inject constructor(
    private val repository: ParkingHistoryRepository
) {
    operator fun invoke(userId: String): Flow<List<ParkingHistory>> {
        if (userId.isBlank()) {
            throw ParkingHistoryError.InvalidUserId
        }

        return repository.observeHistoriesByUserId(userId)
    }
}

class ObserveRecentHistoriesUseCase @Inject constructor(
    private val repository: ParkingHistoryRepository
) {
    operator fun invoke(limit: Int = 5): Flow<List<ParkingHistory>> {
        return repository.observeAllHistories(limit)
    }
}

class GetHistoryDetailUseCase @Inject constructor(
    private val repository: ParkingHistoryRepository
) {
    suspend operator fun invoke(historyId: String): ParkingHistory {

        if (historyId.isBlank()) {
            throw ParkingHistoryError.InvalidHistoryId
        }

        return repository.getHistoryDetail(historyId)
    }
}