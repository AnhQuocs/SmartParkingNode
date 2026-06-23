package com.example.smarttrafficradar.features.history.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val historyId = intent.getStringExtra("historyId") ?: ""

        setContent {
            HistoryDetailScreen(
                historyId = historyId,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun HistoryDetailScreen(
    historyId: String,
    onBackClick: () -> Unit,
    parkingHistoryViewModel: ParkingHistoryViewModel = hiltViewModel()
) {
    val state by parkingHistoryViewModel.detailState.collectAsState()

    LaunchedEffect(historyId) {
        parkingHistoryViewModel.getHistoryDetail(historyId)
    }

    HistoryDetailState(
        state = state,
        onBack = onBackClick
    )
}