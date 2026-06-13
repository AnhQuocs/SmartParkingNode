package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel

@Composable
fun DashboardScreen(
    uid: String,
    userProfileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val uiState by userProfileViewModel.profileState.collectAsState()

    LaunchedEffect(uid) {
        userProfileViewModel.loadUserProfile(uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        DashboardState(
            state = uiState
        )
    }
}