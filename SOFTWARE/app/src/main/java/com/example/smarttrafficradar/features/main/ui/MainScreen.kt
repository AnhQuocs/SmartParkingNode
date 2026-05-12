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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.smarttrafficradar.components.AppBottomBar
import com.example.smarttrafficradar.features.control.ControlScreen
import com.example.smarttrafficradar.features.dashboard.ui.DashboardScreen
import com.example.smarttrafficradar.features.status.StatusScreen
import com.example.smarttrafficradar.features.violation.presentation.ui.ViolationScreen

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var previousTabIndex by remember { mutableIntStateOf(0) }

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

                1 -> ViolationScreen()

                2 -> ControlScreen()

                3 -> StatusScreen()
            }
        }
    }
}