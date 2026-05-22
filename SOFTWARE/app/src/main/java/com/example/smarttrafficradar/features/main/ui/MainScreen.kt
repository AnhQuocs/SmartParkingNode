package com.example.smarttrafficradar.features.main.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.components.AppBottomBar
import com.example.smarttrafficradar.features.control.ControlScreen
import com.example.smarttrafficradar.features.dashboard.ui.DashboardScreen
import com.example.smarttrafficradar.features.status.StatusScreen
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigState
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigViewModel
import com.example.smarttrafficradar.features.violation.presentation.ui.ViolationScreen
import com.example.smarttrafficradar.features.violation.presentation.viewmodel.ViolationViewModel

@Composable
fun MainScreen(
    systemConfigViewModel: SystemConfigViewModel = hiltViewModel(),
    violationViewModel: ViolationViewModel = hiltViewModel()
) {
    val violationState by violationViewModel.violationState.collectAsState()
    val systemConfigState by systemConfigViewModel.state.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

    val vMaxThresholds = when (val state = systemConfigState) {
        is SystemConfigState.Success -> state.config.vMaxThreshold
        else -> 0
    }

    Scaffold(
        bottomBar = {
            AppBottomBar(
                currentIndex = selectedTabIndex,
                onTabSelected = { newIndex ->
                    previousTabIndex = selectedTabIndex
                    selectedTabIndex = newIndex
                }
            )
        }
    ) { paddingValues ->

        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                } else {
                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "StepTransition",
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) { step ->
            when (step) {
                0 -> DashboardScreen()

                1 -> ViolationScreen(
                    violationState = violationState,
                    vMaxThresholds = vMaxThresholds
                )

                2 -> ControlScreen()

                3 -> StatusScreen()
            }
        }
    }
}