package com.example.smarttrafficradar.features.payment.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryDetailActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryState
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
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

                },
                onViewHistory = {

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

            when (val currentState = state) {
                is PaymentState.Idle -> {
                    Button(
                        onClick = {
                            paymentViewModel.createPaymentUrl(uid, amount)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Thanh toán 50.000đ qua MoMo")
                    }
                }

                is PaymentState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang khởi tạo thanh toán...")
                }

                is PaymentState.Error -> {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { paymentViewModel.resetState() }) {
                        Text("Thử lại")
                    }
                }

                is PaymentState.Success -> Unit
            }
        }

        if (state is PaymentState.Success) {
            val currentState = state as PaymentState.Success

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true // Bắt buộc cho MoMo
                        settings.domStorageEnabled = true

                        settings.userAgentString =
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?, request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false

                                // Xử lý Deep Link để mở app MoMo
                                if (url.startsWith("momo://")) {
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                        ctx.startActivity(intent)
                                        true
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            ctx,
                                            "Vui lòng cài đặt ứng dụng MoMo",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        true
                                    }
                                }
                                // Tiếp tục load link Web bình thường
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                },
                update = { webView ->
                    // Load URL khi state thành công
                    webView.loadUrl(currentState.payUrl)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}