package com.example.smarttrafficradar.features.notification.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.presentation.viewmodel.NotificationViewModel
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    onBackClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
    viewModel: NotificationViewModel
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.notifications_title),
                        style = MaterialTheme.typography.s16.semiBold(),
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (state.notifications.any { !it.isRead }) {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = stringResource(id = R.string.mark_all_as_read),
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SmartBlue
                )
            } else if (state.notifications.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_notifications),
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onClick = { 
                                viewModel.selectNotification(notification)
                                onNotificationClick(notification)
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = Dimen.PaddingM),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
