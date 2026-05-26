package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigState
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.DarkBackground
import com.example.smarttrafficradar.ui.theme.NeonGreen
import com.example.smarttrafficradar.ui.theme.OrangePrimary
import com.example.smarttrafficradar.ui.theme.SurfaceDark
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatusScreen(
    systemConfigViewModel: SystemConfigViewModel = hiltViewModel()
) {
    var showNetworkSetup by remember { mutableStateOf(false) }

    if (showNetworkSetup) {
        NetworkSetupScreen(onBack = { showNetworkSetup = false })
    } else {
        StatusContent(
            onOpenSetup = { showNetworkSetup = true },
            viewModel = systemConfigViewModel
        )
    }
}

@Composable
fun StatusContent(
    onOpenSetup: () -> Unit,
    viewModel: SystemConfigViewModel
) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
            .padding(Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.L))

            ConnectionStatusCard(state)

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            Button(
                onClick = onOpenSetup,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightDefault),
                shape = RoundedCornerShape(AppShape.ShapeM),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.setup_network),
                    style = MaterialTheme.typography.s16.bold()
                )
            }
        }
    }
}

@Composable
fun ConnectionStatusCard(state: SystemConfigState) {
    Card(
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(R.string.connection_status),
                style = MaterialTheme.typography.s18.bold(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            when (state) {
                is SystemConfigState.Success -> {
                    val config = state.config
                    
                    StatusRow(
                        label = stringResource(R.string.wifi_status),
                        statusText = if (config.isOnline) stringResource(R.string.connected) else stringResource(R.string.disconnected),
                        statusColor = if (config.isOnline) NeonGreen else Color.Red,
                        showCheck = config.isOnline
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    StatusRow(
                        label = stringResource(R.string.firebase_status),
                        statusText = if (config.isOnline) stringResource(R.string.authenticated) else stringResource(R.string.unauthenticated),
                        statusColor = if (config.isOnline) NeonGreen else Color.Red,
                        showCheck = config.isOnline
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.last_sync),
                            style = MaterialTheme.typography.s16,
                            color = Color.Gray
                        )
                        Text(
                            text = formatTimestamp(config.lastPing),
                            style = MaterialTheme.typography.s16,
                            color = Color.White
                        )
                    }
                }
                is SystemConfigState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = OrangePrimary)
                }
                is SystemConfigState.Error -> {
                    Text(text = state.message.asString(), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun StatusRow(
    label: String,
    statusText: String,
    statusColor: Color,
    showCheck: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.s16,
            color = Color.Gray
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showCheck) {
                Text(text = "✓ ", color = statusColor, fontWeight = FontWeight.Bold)
            }
            Text(
                text = statusText,
                style = MaterialTheme.typography.s16.bold(),
                color = statusColor
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val sdf = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
