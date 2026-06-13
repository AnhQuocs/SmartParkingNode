package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.utils.s15

@Composable
fun DashboardState(state: UserProfileState) {
    when (state) {
        is UserProfileState.Idle, is UserProfileState.Loading -> {
            CircularProgressIndicator()
        }

        is UserProfileState.Success -> {
            val profile = state.profile

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
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