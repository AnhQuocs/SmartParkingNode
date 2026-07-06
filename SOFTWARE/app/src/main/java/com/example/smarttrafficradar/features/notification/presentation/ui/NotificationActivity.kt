package com.example.smarttrafficradar.features.notification.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.notification.presentation.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val notificationViewModel: NotificationViewModel = hiltViewModel()

            var isShowDetail by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NotificationListScreen(
                    viewModel = notificationViewModel,
                    onNotificationClick = {
                        isShowDetail = true
                    },
                    onBackClick = { finish() }
                )

                if (isShowDetail) {
                    NotificationDetailScreen(
                        onBackClick = { isShowDetail = false }
                    )
                }
            }
        }
    }
}