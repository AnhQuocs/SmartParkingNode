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
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingStatus
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.utils.s15

@Composable
fun DashboardState(state: UserProfileState) {
    val scrollState = rememberScrollState()

    when (state) {
        is UserProfileState.Idle, is UserProfileState.Loading -> {
            CircularProgressIndicator()
        }

        is UserProfileState.Success -> {
            val profile = state.profile

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Background)
                    .padding(bottom = Dimen.PaddingM)
                    .verticalScroll(scrollState)
            ) {
                DashboardTopBar(
                    fullName = profile.fullName,
                    type = profile.memberType,
                    identifier = profile.identifier
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
                    currentDebt = profile.currentDebt.toString(),
                    onPayClick = {

                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                /** BEGIN MOCK DATA */

                val parkingHistories = listOf(
                    ParkingHistory(
                        id = "history_001",
                        userId = "uid_001",
                        rfidUid = "A1B2C3D4",
                        checkInTime = 1750148130L,
                        checkOutTime = null,
                        durationMinutes = 0,
                        fee = 0,
                        status = ParkingStatus.CHECK_IN,
                        createdAt = 1750148130L,
                        updatedAt = 1750148130L
                    ),
                    ParkingHistory(
                        id = "history_002",
                        userId = "uid_001",
                        rfidUid = "A1B2C3D4",
                        checkInTime = 1750234530L,
                        checkOutTime = 1750243530L,
                        durationMinutes = 150,
                        fee = 5000,
                        status = ParkingStatus.CHECK_OUT,
                        createdAt = 1750234530L,
                        updatedAt = 1750243530L
                    ),
                    ParkingHistory(
                        id = "history_003",
                        userId = "uid_001",
                        rfidUid = "A1B2C3D4",
                        checkInTime = 1750320930L,
                        checkOutTime = null,
                        durationMinutes = 0,
                        fee = 0,
                        status = ParkingStatus.CHECK_IN,
                        createdAt = 1750320930L,
                        updatedAt = 1750320930L
                    )
                )

                QuickActionsSection(
                    onPayment = {},
                    onRegisterCard = {},
                    onSupport = {},
                    onViewHistory = {}
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                RecentActivitiesSection(
                    histories = parkingHistories,
                    onDetail = { historyId ->

                    },
                    onSeeAll = {

                    }
                )

                /** END MOCK DATA */
            }
        }

        is UserProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.uiText.asString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.s15
                )
            }
        }
    }
}