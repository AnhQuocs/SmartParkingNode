package com.example.smarttrafficradar.features.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.components.AdminBottomBar
import com.example.smarttrafficradar.features.analytics.presentation.ui.AnalyticsScreen
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthState
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.control.presentation.ui.ControlScreen
import com.example.smarttrafficradar.features.dashboard.presentation.ui.admin.MonitorScreen
import com.example.smarttrafficradar.features.management.presentation.ui.ManagementScreen
import com.example.smarttrafficradar.features.profile.presentation.ui.user.LogoutDialog

@Composable
fun AdminMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val currentUser by authViewModel.currentUser.collectAsState()
    var isShowDialog by remember { mutableStateOf(false) }

    val authState by authViewModel.state.collectAsState()
    LaunchedEffect(authState) {
        if (authState is AuthState.SignedOut) {
            navController.navigate("auth") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
            )
        },
        bottomBar = {
            AdminBottomBar(
                currentIndex = selectedTabIndex,
                onTabSelected = { selectedTabIndex = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (selectedTabIndex) {
                0 -> currentUser?.let {
                    MonitorScreen(
                        user = it,
                        onLogout = { isShowDialog = true }
                    )
                }

                1 -> {
                    ManagementScreen()
                }

                2 -> {
                    ControlScreen()
                }

                3 -> {
                    AnalyticsScreen()
                }
            }

            if (isShowDialog) {
                LogoutDialog(onDismiss = { isShowDialog = false }, onConfirm = {
                    authViewModel.signOut()
                    isShowDialog = false
                })
            }
        }
    }
}