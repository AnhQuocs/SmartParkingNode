package com.example.smarttrafficradar.features.notification.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.notification.presentation.viewmodel.NotificationViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notification by viewModel.selectedNotification.collectAsState()

    BackHandler { onBackClick() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = stringResource(id = R.string.notification_detail_title), 
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartBlue
                )
            )
        }
    ) { paddingValues ->
        notification?.let { item ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.s18,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = formatTimestamp(item.timestamp),
                    style = MaterialTheme.typography.s12,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = Dimen.PaddingS)
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Text(
                    text = item.body,
                    style = MaterialTheme.typography.s16,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
