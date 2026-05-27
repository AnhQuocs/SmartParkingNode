package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiNetwork
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiStatus
import com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel.NetworkState
import com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel.NetworkViewModel
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.DarkBackground
import com.example.smarttrafficradar.ui.theme.NeonGreen
import com.example.smarttrafficradar.ui.theme.OrangePrimary
import com.example.smarttrafficradar.ui.theme.SurfaceDark
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSetupScreen(
    onBack: () -> Unit,
    viewModel: NetworkViewModel = hiltViewModel()
) {
    val networkState by viewModel.state.collectAsState()
    val wifiConfig by viewModel.wifiConfig.collectAsState()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var showForgetDialog by remember { mutableStateOf(false) }
    var selectedSsid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    BackHandler { onBack() }

    Scaffold(
        topBar = {
            NetworkSetupTopBar(onBackClick = onBack)
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(Dimen.PaddingM)
        ) {
            // Task/Status monitoring
            wifiConfig?.let { config ->
                if (config.status != WifiStatus.IDLE) {
                    SetupStatusCard(config)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            when (val state = networkState) {
                is NetworkState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = OrangePrimary)
                    }
                }

                is NetworkState.Success -> {
                    val monitor = state.monitor
                    val currentSsid = monitor.connectionStatus.currentSsid

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(R.string.current_connection),
                            style = MaterialTheme.typography.s18.bold(),
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        val currentWifi =
                            monitor.availableNetworks.find { it.ssid == currentSsid }

                        if (currentWifi != null) {
                            WifiItem(
                                network = currentWifi,
                                isCurrent = true,
                                onClick = {},
                                onLongClick = {}
                            )
                        } else if (currentSsid.isNotEmpty()) {
                            WifiItem(
                                network = WifiNetwork(
                                    currentSsid,
                                    0,
                                    "Unknown",
                                    0,
                                    "Unknown",
                                    0
                                ),
                                isCurrent = true,
                                onClick = {},
                                onLongClick = {}
                            )
                        }

                        Text(
                            text = stringResource(R.string.available_networks),
                            style = MaterialTheme.typography.s18.bold(),
                            color = Color.White,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )

                        monitor.availableNetworks
                            .filter { it.ssid != currentSsid }
                            .forEach { network ->
                                WifiItem(
                                    network = network,
                                    isCurrent = false,
                                    onClick = {
                                        viewModel.handleWifiSelection(network.ssid) {
                                            selectedSsid = network.ssid
                                            showPasswordDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        selectedSsid = network.ssid
                                        showForgetDialog = true
                                    }
                                )
                            }
                    }
                }

                is NetworkState.Error -> {
                    Text(text = state.message.asString(), color = Color.Red)
                }

                else -> {}
            }
        }
    }

    // Password Dialog
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(stringResource(R.string.enter_password_for, selectedSsid)) },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.connectToWifi(selectedSsid, password)
                        showPasswordDialog = false
                        password = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(stringResource(R.string.connect))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Forget Dialog
    if (showForgetDialog) {
        AlertDialog(
            onDismissRequest = { showForgetDialog = false },
            title = { Text(stringResource(R.string.forget_network)) },
            text = { Text(stringResource(R.string.forget_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.forgetWifi(selectedSsid)
                        showForgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.forget), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun SetupStatusCard(config: com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.current_task_format, config.ssid),
                style = MaterialTheme.typography.s16.bold(),
                color = Color.White
            )
            val statusColor = when (config.status) {
                WifiStatus.SUCCESS -> NeonGreen
                WifiStatus.FAILED -> Color.Red
                WifiStatus.PENDING -> OrangePrimary
                else -> Color.Gray
            }
            val statusLabel = when (config.status) {
                WifiStatus.IDLE -> stringResource(R.string.wifi_status_idle)
                WifiStatus.PENDING -> stringResource(R.string.wifi_status_pending)
                WifiStatus.SUCCESS -> stringResource(R.string.wifi_status_success)
                WifiStatus.FAILED -> stringResource(R.string.wifi_status_failed)
            }
            Text(
                text = stringResource(R.string.status_format, statusLabel),
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
            config.message?.let {
                Text(text = it, color = Color.LightGray, style = MaterialTheme.typography.s16)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WifiItem(
    network: WifiNetwork,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) OrangePrimary.copy(alpha = 0.1f) else SurfaceDark
        ),
        border = if (isCurrent) BorderStroke(1.dp, OrangePrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_wifi),
                contentDescription = null,
                tint = if (isCurrent) OrangePrimary else Color.White,
                modifier = Modifier.size(Dimen.SizeM)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = network.ssid,
                    color = Color.White,
                    style = MaterialTheme.typography.s16.bold()
                )
                Text(
                    text = stringResource(R.string.signal_format, network.signalPct),
                    color = Color.Gray
                )
            }
            if (isCurrent) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonGreen,
                    modifier = Modifier.size(20.dp)
                )
            } else if (network.security != "Open") {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
