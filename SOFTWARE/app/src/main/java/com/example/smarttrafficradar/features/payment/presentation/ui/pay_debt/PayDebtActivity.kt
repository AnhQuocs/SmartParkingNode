package com.example.smarttrafficradar.features.payment.presentation.ui.pay_debt

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayDebtActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = intent.getStringExtra("uid") ?: ""

        setContent {
            PayDebtScreen(
                uid = uid,
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun PayDebtScreen(
    uid: String,
    onBack: () -> Unit,
    profileViewModel: UserProfileViewModel = hiltViewModel(),
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.profileState.collectAsState()

    LaunchedEffect(uid) {
        profileViewModel.loadUserProfile(uid)
    }

    Scaffold(
        topBar = {
            PayDebtTopBar(onBack = onBack)
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            PayDebtState(
                profileState = profileState,
                paymentViewModel = paymentViewModel
            )
        }
    }
}