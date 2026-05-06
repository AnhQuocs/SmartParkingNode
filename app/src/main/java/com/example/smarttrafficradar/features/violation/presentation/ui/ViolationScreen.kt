package com.example.smarttrafficradar.features.violation.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.components.AppHeader
import com.example.smarttrafficradar.features.dashboard.ui.FivePetalSpiralLoader
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.presentation.viewmodel.ViolationState
import com.example.smarttrafficradar.features.violation.presentation.viewmodel.ViolationViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ViolationScreen(
    violationViewModel: ViolationViewModel = hiltViewModel()
) {
    val violationState by violationViewModel.violationState.collectAsState()

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(val state = violationState) {
            is ViolationState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FivePetalSpiralLoader()
                }
            }

            is ViolationState.Success -> {
                val violations = state.violations

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimen.PaddingSM)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    violations.forEachIndexed { index, violation ->
                        AnimatedViolationCard(index = index, violation = violation)
                    }
                }
            }

            is ViolationState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppHeader(
                        text = state.message.asString(),
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedViolationCard(index: Int, violation: Violation) {
    val alpha = remember { Animatable(0f) }
    val translationX = remember { Animatable(-100f) }

    LaunchedEffect(Unit) {
        delay(index * 100L)

        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            )
        }

        launch {
            translationX.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    ViolationCard(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationX = translationX.value
        },
        violation = violation
    )
}