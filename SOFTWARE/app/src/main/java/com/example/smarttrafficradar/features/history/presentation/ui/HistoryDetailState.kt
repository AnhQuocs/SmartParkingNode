package com.example.smarttrafficradar.features.history.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.features.history.presentation.viewmodel.HistoryDetailState
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.utils.s15

@Composable
fun HistoryDetailState(
    vehicleType: VehicleType,
    state: HistoryDetailState,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    when (state) {
        is HistoryDetailState.Loading, HistoryDetailState.Idle -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightPrimary)
            }
        }

        is HistoryDetailState.Success -> {
            val history = state.history

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Background)
                    .verticalScroll(scrollState)
                    .padding(bottom = Dimen.PaddingM)
            ) {
                HistoryTopBar(
                    history = history,
                    onBack = onBack
                )

                ParkingInformationCard(
                    vehicleType = vehicleType,
                    parkingHistory = history,
                    modifier = Modifier
                        .offset(y = (-32).dp)
                        .padding(horizontal = Dimen.PaddingL),
                )

                TimeDetailsCard(
                    history = history,
                    modifier = Modifier
                        .offset(y = (-16).dp)
                        .padding(horizontal = Dimen.PaddingL)
                )
            }
        }

        is HistoryDetailState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.uiText.asString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.s15
                )
            }
        }
    }
}