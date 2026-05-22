<<<<<<< HEAD
package com.example.smarttrafficradar.features.violation.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppHeader
import com.example.smarttrafficradar.features.dashboard.ui.FivePetalSpiralLoader
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.presentation.viewmodel.ViolationState
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanBackground
import com.example.smarttrafficradar.ui.theme.CyanBorder
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.DarkBackground
import com.example.smarttrafficradar.utils.s16
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ViolationScreen(violationState: ViolationState, vMaxThresholds: Int) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        when (violationState) {
            is ViolationState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FivePetalSpiralLoader()
                }
            }

            is ViolationState.Success -> {
                val violations = violationState.violations.take(8)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimen.PaddingSM)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    Spacer(modifier = Modifier.height(AppSpacing.XXS))

                    ViolationTopBar(size = violations.size)

                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    violations.forEachIndexed { index, violation ->
                        AnimatedViolationCard(index = index, violation = violation, vMaxThresholds = vMaxThresholds)
                    }

                    OutlinedButton(
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.HeightDefault)
                            .clip(RoundedCornerShape(AppShape.ShapeM)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanBackground
                        ),
                        border = BorderStroke(1.dp, color = CyanBorder),
                        shape = RoundedCornerShape(AppShape.ShapeL)
                    ) {
                        Text(
                            text = stringResource(id = R.string.see_all),
                            style = MaterialTheme.typography.s16,
                            color = CyanPrimary
                        )
                    }
                }
            }

            is ViolationState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppHeader(
                        text = violationState.message.asString(),
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedViolationCard(index: Int, violation: Violation, vMaxThresholds: Int) {
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
        violation = violation,
        vMaxThresholds = vMaxThresholds
    )
=======
package com.example.smarttrafficradar.features.violation.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppHeader
import com.example.smarttrafficradar.features.dashboard.ui.FivePetalSpiralLoader
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.presentation.viewmodel.ViolationState
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanBackground
import com.example.smarttrafficradar.ui.theme.CyanBorder
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.DarkBackground
import com.example.smarttrafficradar.utils.s16
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ViolationScreen(violationState: ViolationState) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        when (violationState) {
            is ViolationState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FivePetalSpiralLoader()
                }
            }

            is ViolationState.Success -> {
                val violations = violationState.violations.take(6)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimen.PaddingSM)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    Spacer(modifier = Modifier.height(AppSpacing.XXS))

                    ViolationTopBar(size = violations.size)

                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    violations.forEachIndexed { index, violation ->
                        AnimatedViolationCard(
                            index = index,
                            violation = violation
                        )
                    }

                    OutlinedButton(
                        onClick = {

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.HeightDefault)
                            .clip(RoundedCornerShape(AppShape.ShapeM)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanBackground
                        ),
                        border = BorderStroke(1.dp, color = CyanBorder),
                        shape = RoundedCornerShape(AppShape.ShapeL)
                    ) {
                        Text(
                            text = stringResource(id = R.string.see_all),
                            style = MaterialTheme.typography.s16,
                            color = CyanPrimary
                        )
                    }
                }
            }

            is ViolationState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppHeader(
                        text = violationState.message.asString(),
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
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}