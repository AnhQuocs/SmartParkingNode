package com.example.smarttrafficradar.features.dashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.smarttrafficradar.components.AppHeader
import com.example.smarttrafficradar.features.dashboard.viewmodel.DashboardState
import com.example.smarttrafficradar.ui.dimens.Dimen

@Composable
fun DashboardSection(state: DashboardState) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FivePetalSpiralLoader()
            }
        }

        state.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AppHeader(
                    text = state.error.asString(),
                    color = Color.Red
                )
            }
        }

        state.dailyStats != null && state.liveTracking != null -> {
            val dailyStats = state.dailyStats
            val liveTracking = state.liveTracking

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimen.PaddingM),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DashboardHeader()

                RadarView(
                    currentSpeed = liveTracking.lastVehicle.speedKmh,
                    isViolation = liveTracking.lastVehicle.isViolation
                )

                SpeedAnalysisChart(
                    speedEntries = state.liveTracking.recentEntries.map { it.speed to it.isViolation },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}