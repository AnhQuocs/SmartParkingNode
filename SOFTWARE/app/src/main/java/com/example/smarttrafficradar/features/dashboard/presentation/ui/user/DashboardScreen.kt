package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryDetailActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import com.example.smarttrafficradar.features.notification.presentation.viewmodel.NotificationViewModel
import com.example.smarttrafficradar.features.payment.presentation.ui.pay_debt.PayDebtActivity
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel

@Composable
fun DashboardScreen(
    uid: String,
    onRegisterCard: () -> Unit,
    onViewHistory: () -> Unit,
    onPayment: () -> Unit,
    onSupport: () -> Unit,
    onNotificationClick: () -> Unit,
    userProfileViewModel: UserProfileViewModel = hiltViewModel(),
    parkingHistoryViewModel: ParkingHistoryViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val profileState by userProfileViewModel.profileState.collectAsState()
    val historyState by parkingHistoryViewModel.historyState.collectAsState()
    val notificationState by notificationViewModel.state.collectAsState()

    val notifications = notificationState.notifications

    LaunchedEffect(uid) {
        userProfileViewModel.loadUserProfile(uid)
        parkingHistoryViewModel.observeHistories(userId = uid)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        DashboardState(
            profileState = profileState,
            historyState = historyState,
            onDetail = { historyId ->
                val profile = (profileState as? UserProfileState.Success)?.profile
                val vehicleType = profile?.vehicleType ?: VehicleType.MOTORBIKE

                val intent = Intent(context, HistoryDetailActivity::class.java)
                    .putExtra("historyId", historyId)
                    .putExtra("vehicleType", vehicleType)

                context.startActivity(intent)
            },
            onPayClick = {
                val intent = Intent(context, PayDebtActivity::class.java)
                    .putExtra("uid", uid)

                context.startActivity(intent)
            },
            onPayment = onPayment,
            onRegisterCard = onRegisterCard,
            onSupport = onSupport,
            onViewHistory = onViewHistory,
            onNotificationClick = onNotificationClick,
            notifications = notifications
        )
    }
}