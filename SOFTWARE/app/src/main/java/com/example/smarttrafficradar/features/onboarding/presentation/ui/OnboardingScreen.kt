package com.example.smarttrafficradar.features.onboarding.presentation.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.onboarding.presentation.viewmodel.OnboardingViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.s14
import kotlinx.coroutines.launch

data class OnboardingData(
    val img: Int, val titleRes: Int, val descRes: Int
)

@Composable
fun OnboardingScreen(
    navController: NavController, onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {
    val onboardingItems = listOf(
        OnboardingData(
            R.drawable.spn_onboarding_1, R.string.onboarding_title_1, R.string.onboarding_desc_1
        ), OnboardingData(
            R.drawable.spn_onboarding_2, R.string.onboarding_title_2, R.string.onboarding_desc_2
        ), OnboardingData(
            R.drawable.spn_onboarding_3, R.string.onboarding_title_3, R.string.onboarding_desc_3
        )
    )

    val pagerState = rememberPagerState(pageCount = { onboardingItems.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(top = Dimen.PaddingM)
    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(60.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.app_logo),
//                contentDescription = "Logo",
//                modifier = Modifier.size(60.dp)
//            )
//
//            Text(
//                text = stringResource(R.string.app_name),
//                letterSpacing = 1.sp,
//                style = TextStyle(
//                    brush = Brush.horizontalGradient(
//                        colors = listOf(
//                            Color(0xFF0D47A1),
//                            Color(0xFF1976D2),
//                            Color(0xFF26D9E8)
//                        )
//                    ),
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            )
//        }

        // Pager Content
        HorizontalPager(
            state = pagerState, modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { index ->
            OnboardingPage(item = onboardingItems[index])
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingL, vertical = Dimen.PaddingXL),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicator
            PagerIndicator(
                size = onboardingItems.size,
                currentPage = pagerState.currentPage,
            )

            // Next/Get Started Button
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingItems.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onboardingViewModel.completeOnboarding {
                            navController.navigate("auth") {
                                popUpTo("onboarding_root") { inclusive = true }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPrimary, contentColor = Color.White
                ),
                modifier = Modifier
                    .height(44.dp)
                    .wrapContentWidth()
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingItems.size - 1) stringResource(R.string.onboarding_get_started)
                    else stringResource(R.string.onboarding_next),
                    style = MaterialTheme.typography.s14
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(item: OnboardingData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.PaddingL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightXXL),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(id = item.titleRes),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        Text(
            text = stringResource(id = item.descRes),
            style = MaterialTheme.typography.bodyLarge,
            color = SlateMist,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun PagerIndicator(
    size: Int, currentPage: Int
) {
    Row {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(targetValue = if (isSelected) 25.dp else 10.dp, label = "")

    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) LightPrimary else SlateMist
            )
    )
}