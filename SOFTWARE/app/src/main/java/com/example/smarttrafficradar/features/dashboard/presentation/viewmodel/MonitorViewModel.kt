package com.example.smarttrafficradar.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.dashboard.domain.model.ParkingSummary
import com.example.smarttrafficradar.features.dashboard.domain.usecase.GetParkingSummaryUseCase
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.usecase.ParkingHistoryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val getParkingSummaryUseCase: GetParkingSummaryUseCase,
    private val historyUseCases: ParkingHistoryUseCases
) : ViewModel() {

    val summary: StateFlow<ParkingSummary> = getParkingSummaryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ParkingSummary()
        )

    val recentHistories: StateFlow<List<ParkingHistory>> = historyUseCases.observeRecentHistoriesUseCase(limit = 5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
