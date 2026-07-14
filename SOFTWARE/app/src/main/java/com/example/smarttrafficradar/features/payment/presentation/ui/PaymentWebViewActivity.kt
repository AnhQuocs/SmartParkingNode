package com.example.smarttrafficradar.features.payment.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.presentation.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentWebViewActivity : BaseComponentActivity() {
    private val viewModel: PaymentViewModel by viewModels()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val payUrl = intent.getStringExtra("payUrl") ?: ""
        val uid = intent.getStringExtra("uid") ?: ""
        val amount = intent.getIntExtra("amount", 0)

        setContent {
            val toastMessage = stringResource(id = R.string.please_install_momo)

            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.userAgentString =
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?, request: WebResourceRequest?
                            ): Boolean {
                                val url = request?.url?.toString() ?: return false

                                // Kiểm tra URL trả về từ MoMo (Thường cấu hình trong Backend returnUrl)
                                if (url.contains("resultCode=0")) {
                                    viewModel.saveTransaction(uid, amount)
                                    Toast.makeText(ctx, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
                                    finish()
                                    return true
                                } else if (url.contains("resultCode=")) {
                                    Toast.makeText(ctx, "Giao dịch thất bại hoặc bị hủy", Toast.LENGTH_LONG).show()
                                    finish()
                                    return true
                                }

                                if (url.startsWith("momo://")) {
                                    return try {
                                        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                        ctx.startActivity(intent)
                                        true
                                    } catch (e: Exception) {
                                        Toast.makeText(ctx, toastMessage, Toast.LENGTH_SHORT).show()
                                        true
                                    }
                                }
                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }
                    }
                },
                update = { webView ->
                    if (payUrl.isNotEmpty()) {
                        webView.loadUrl(payUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
