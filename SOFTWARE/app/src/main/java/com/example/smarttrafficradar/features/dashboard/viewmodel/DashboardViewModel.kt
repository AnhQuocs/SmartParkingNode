package com.example.smarttrafficradar.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats
import com.example.smarttrafficradar.features.daily_stats.domain.usecase.GetDailyStatsUseCase
import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import com.example.smarttrafficradar.features.live_tracking.domain.usecase.ObserveLiveTrackingUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DashboardState(
    val dailyStats: DailyStats? = null,
    val liveTracking: LiveTracking? = null,
    val isLoading: Boolean = true,
    val error: UiText? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDailyStatsUseCase: GetDailyStatsUseCase,
    private val observeLiveTrackingUseCase: ObserveLiveTrackingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val today = LocalDate.now().toString()
        val nodeId = "radar_node_01"

        viewModelScope.launch {
            // Dùng combine để gộp 2 "ống nước" dữ liệu lại thành 1
            combine(
                getDailyStatsUseCase(nodeId, "2026-04-17"),
                observeLiveTrackingUseCase(nodeId)
            ) { stats, live ->
                DashboardState(
                    dailyStats = stats,
                    liveTracking = live,
                    isLoading = false
                )
            }.catch { e ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = UiText.StringResource(R.string.error_unexpected)
                )
            }.collect { combinedState ->
                _state.value = combinedState
            }
        }
    }
}