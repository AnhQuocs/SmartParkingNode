package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val networkState by viewModel.networkState.collectAsState()
    val wifiConfig by viewModel.wifiConfig.collectAsState()
    
    var selectedWifi by remember { mutableStateOf<WifiNetwork?>(null) }
    var password by remember { mutableStateOf("") }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.startScanning()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.setup_network), color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Status Info
            wifiConfig?.let { config ->
                if (config.status != WifiStatus.IDLE) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.current_task_format, config.ssid),
                                style = MaterialTheme.typography.s16.bold(),
                                color = Color.White
                            )
                            val statusColor = when(config.status) {
                                WifiStatus.SUCCESS -> NeonGreen
                                WifiStatus.FAILED -> Color.Red
                                WifiStatus.PENDING -> OrangePrimary
                                else -> Color.Gray
                            }
                            
                            val statusLabel = when(config.status) {
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
            }

            Text(
                text = stringResource(R.string.scan_wifi),
                style = MaterialTheme.typography.s18.bold(),
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (val state = networkState) {
                is NetworkState.Scanning -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = OrangePrimary)
                    }
                }
                is NetworkState.Success -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.networks) { network ->
                            WifiItem(network) {
                                selectedWifi = network
                                showPasswordDialog = true
                            }
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

    if (showPasswordDialog && selectedWifi != null) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text(stringResource(R.string.enter_password_for, selectedWifi?.ssid ?: "")) },
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
                        viewModel.connectToWifi(selectedWifi!!.ssid, password)
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
}

@Composable
fun WifiItem(network: WifiNetwork, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(id = R.drawable.ic_wifi), contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = network.ssid, color = Color.White, style = MaterialTheme.typography.s16.bold())
                Text(text = stringResource(R.string.signal_format, network.signalLevel), color = Color.Gray)
            }
            if (network.isSecure) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}
