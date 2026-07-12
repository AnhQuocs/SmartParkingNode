package com.example.smarttrafficradar.features.main.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.components.UserBottomBar
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.dashboard.presentation.ui.user.DashboardScreen
import com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card.RegisterCardActivity
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryScreen
import com.example.smarttrafficradar.features.notification.presentation.ui.NotificationActivity
import com.example.smarttrafficradar.features.payment.presentation.ui.PaymentScreen
import com.example.smarttrafficradar.features.profile.presentation.ui.ProfileScreen
import com.example.smarttrafficradar.features.profile.presentation.ui.SupportCenterActivity
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel

@Composable
fun UserMainScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: UserProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val currentUser by authViewModel.currentUser.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            profileViewModel.loadUserProfile(it.uid)
        }
    }

    val profileState by profileViewModel.profileState.collectAsState()
    val profile = (profileState as? UserProfileState.Success)?.profile

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            )
        },
        bottomBar = {
            UserBottomBar(
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
                0 -> {
                    currentUser?.let { user ->
                        DashboardScreen(
                            uid = user.uid,
                            onViewHistory = {
                                selectedTabIndex = 1
                            },
                            onSupport = {
                                context.startActivity(Intent(context, SupportCenterActivity::class.java))
                            },
                            onRegisterCard = {
                                val intent = Intent(context, RegisterCardActivity::class.java).apply {
                                    putExtra("uid", user.uid)
                                }
                                context.startActivity(intent)
                            },
                            onPayment = {
                                selectedTabIndex = 2
                            },
                            onNotificationClick = {
                                val intent = Intent(context, NotificationActivity::class.java)
                                context.startActivity(intent)
                            }
                        )
                    }
                }

                1 -> {
                    HistoryScreen()
                }

                2 -> {
                    PaymentScreen(
                        uid = currentUser?.uid ?: "",
                        amount = profile?.currentDebt ?: 0
                    )
                }

                3 -> {
                    profile?.let {
                        ProfileScreen(
                            profile = it,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}
