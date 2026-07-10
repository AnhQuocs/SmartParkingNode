package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryState
import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.utils.s15

@Composable
fun DashboardState(
    profileState: UserProfileState,
    historyState: ParkingHistoryState,
    onRegisterCard: () -> Unit,
    onViewHistory: () -> Unit,
    onPayment: () -> Unit,
    onSupport: () -> Unit,
    onDetail: (String) -> Unit,
    onNotificationClick: () -> Unit,
    notifications: List<Notification>,
    onPayClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    when (profileState) {
        is UserProfileState.Idle, is UserProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightPrimary)
            }
        }

        is UserProfileState.Success -> {
            val profile = profileState.profile

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Background)
                    .verticalScroll(scrollState)
                    .padding(bottom = Dimen.PaddingM)
            ) {
                DashboardTopBar(
                    fullName = profile.fullName,
                    type = profile.memberType,
                    identifier = profile.identifier,
                    onNotificationClick = onNotificationClick,
                    notifications = notifications
                )

                ParkingCard(
                    modifier = Modifier
                        .offset(y = (-16).dp)
                        .padding(horizontal = Dimen.PaddingM),
                    isActive = profile.isActive,
                    rfidUid = profile.rfidUid,
                    vehicleType = profile.vehicleType
                )

                DebtCard(
                    currentDebt = profile.currentDebt,
                    onPayClick = onPayClick
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                QuickActionsSection(
                    onPayment = onPayment,
                    onRegisterCard = onRegisterCard,
                    onSupport = onSupport,
                    onViewHistory = onViewHistory
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                RecentActivitiesState(
                    state = historyState,
                    onDetail = onDetail
                )
            }
        }

        is UserProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profileState.uiText.asString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.s15
                )
            }
        }
    }
}