package com.example.smarttrafficradar.features.payment.presentation.ui.pay_history

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PayHistoryActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PayHistoryScreen(
                onBack = { finish() }
            )
        }
    }
}

@Composable
fun PayHistoryScreen(
    onBack: () -> Unit,
    paymentViewModel: PaymentViewModel = hiltViewModel()
) {
    val paymentSummary by paymentViewModel.paymentSummary.collectAsState()
    val paymentHistories by paymentViewModel.paymentHistories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        PayHistoryTopBar(
            paymentSummary = paymentSummary,
            onBack = onBack
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimen.PaddingM)
        ) {
            items(paymentHistories) { history ->
                PayHistoryItem(history = history)
                Spacer(modifier = Modifier.height(AppSpacing.S))
            }
        }
    }
}
