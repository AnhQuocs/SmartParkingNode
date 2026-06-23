package com.example.smarttrafficradar.features.history.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.smarttrafficradar.features.history.presentation.viewmodel.HistoryDetailState
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.utils.s15

@Composable
fun HistoryDetailState(state: HistoryDetailState, onBack: () -> Unit) {
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
            ) {
                HistoryTopBar(
                    history = history,
                    onBack = onBack
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