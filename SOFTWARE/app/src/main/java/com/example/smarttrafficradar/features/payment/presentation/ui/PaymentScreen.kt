package com.example.smarttrafficradar.features.payment.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentState
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PaymentScreen(
    uid: String,
    amount: Int,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.paymentState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val currentState = state) {
            is PaymentState.Idle -> {
                Button(
                    onClick = {
                        viewModel.createPaymentUrl(uid, amount)
                    }, modifier = Modifier.padding(16.dp)
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
                Button(onClick = { viewModel.resetState() }) {
                    Text("Thử lại")
                }
            }

            is PaymentState.Success -> {
                AndroidView(
                    factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true // Bắt buộc cho MoMo
                        settings.domStorageEnabled = true

                        settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"

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
                }, update = { webView ->
                    // Load URL khi state thành công
                    webView.loadUrl(currentState.payUrl)
                }, modifier = Modifier.fillMaxSize() // Chiếm toàn bộ màn hình khi thanh toán
                )
            }
        }
    }
}