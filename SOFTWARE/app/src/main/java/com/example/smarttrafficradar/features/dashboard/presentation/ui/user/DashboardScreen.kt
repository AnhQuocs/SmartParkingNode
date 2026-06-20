package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryDetailActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel

@Composable
fun DashboardScreen(
    uid: String,
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    parkingHistoryViewModel: ParkingHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val profileState by userProfileViewModel.profileState.collectAsState()
    val historyState by parkingHistoryViewModel.historyState.collectAsState()

    LaunchedEffect(uid) {
        userProfileViewModel.loadUserProfile(uid)
        parkingHistoryViewModel.observeHistories(userId = uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        DashboardState(
            profileState = profileState,
            historyState = historyState,
            onDetail = { historyId ->
                val intent = Intent(context, HistoryDetailActivity::class.java)
                    .putExtra("historyId", historyId)
                context.startActivity(intent)
            }
        )
    }
}