package com.example.smarttrafficradar.features.main.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.components.AdminBottomBar
import com.example.smarttrafficradar.features.analytics.presentation.ui.AnalyticsScreen
import com.example.smarttrafficradar.features.control.ControlScreen
import com.example.smarttrafficradar.features.dashboard.presentation.ui.admin.MonitorScreen
import com.example.smarttrafficradar.features.management.presentation.ui.ManagementScreen
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListViewModel

@Composable
fun AdminMainScreen(
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
                0 -> MonitorScreen(
                    navController = navController
                )

                1 -> {
                    ManagementScreen()
                }

                2 -> {
                    ControlScreen(
                        showNetworkSetup = {

                        }
                    )
                }

                3 -> {
                    AnalyticsScreen()
                }
            }
        }
    }
}