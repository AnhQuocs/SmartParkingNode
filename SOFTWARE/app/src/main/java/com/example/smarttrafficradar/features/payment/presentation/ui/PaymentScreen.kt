package com.example.smarttrafficradar.features.payment.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryDetailActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryState
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import com.example.smarttrafficradar.features.payment.presentation.ui.pay_debt.PayDebtActivity
import com.example.smarttrafficradar.features.payment.presentation.ui.pay_history.PayHistoryActivity
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentState
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.theme.Background

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentScreen(
    uid: String,
    amount: Int,
    paymentViewModel: PaymentViewModel = hiltViewModel(),
    historyViewModel: ParkingHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val historyState by historyViewModel.historyState.collectAsState()
    val histories = (historyState as? ParkingHistoryState.Success)?.histories
    val debtHistories = histories?.filter { history -> history.fee != 0 }

    LaunchedEffect(uid) {
        historyViewModel.observeHistories(uid)
    }

    val scrollState = rememberScrollState()

    val state by paymentViewModel.paymentState.collectAsState()
    val paymentSummary by paymentViewModel.paymentSummary.collectAsState()

    LaunchedEffect(state) {
        if (state is PaymentState.Success) {
            val payUrl = (state as PaymentState.Success).payUrl
            val intent = Intent(context, PaymentWebViewActivity::class.java).apply {
                putExtra("payUrl", payUrl)
                putExtra("uid", uid)
                putExtra("amount", amount)
            }
            context.startActivity(intent)
            paymentViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            PaymentTopBar(
                currentDebt = amount,
                paid = paymentSummary.totalPaid,
                lastPaidAt = paymentSummary.lastPaidAt
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            PaymentTabs(
                onPayment = {
                    val intent = Intent(context, PayDebtActivity::class.java)
                        .putExtra("uid", uid)

                    context.startActivity(intent)
                },
                onViewHistory = {
                    val intent = Intent(context, PayHistoryActivity::class.java)

                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            DebtHistorySection(
                debtHistories = debtHistories,
                onDetail = { historyId ->
                    val intent = Intent(context, HistoryDetailActivity::class.java)
                        .putExtra("historyId", historyId)

                    context.startActivity(intent)
                },
                onSeeAll = {
                    val intent = Intent(context, AllDebtHistoryActivity::class.java)
                        .putExtra("uid", uid)

                    context.startActivity(intent)
                }
            )
        }
    }
}
