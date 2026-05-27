package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigState
import com.example.smarttrafficradar.features.system_config.presentation.viewmodel.SystemConfigViewModel
import com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel.NetworkState
import com.example.smarttrafficradar.features.system_monitor.presentation.viewmodel.NetworkViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.DarkBackground
import com.example.smarttrafficradar.ui.theme.OrangePrimary

@Composable
fun StatusScreen(
    systemConfigViewModel: SystemConfigViewModel = hiltViewModel(),
    networkViewModel: NetworkViewModel = hiltViewModel()
) {
    var showNetworkSetup by remember { mutableStateOf(false) }

    if (showNetworkSetup) {
        NetworkSetupScreen(onBack = { showNetworkSetup = false })
    } else {
        StatusContent(
            configViewModel = systemConfigViewModel,
            networkViewModel = networkViewModel
        )
    }
}

@Composable
fun StatusContent(
    configViewModel: SystemConfigViewModel,
    networkViewModel: NetworkViewModel
) {
    val configState by configViewModel.state.collectAsState()
    val networkState by networkViewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.L))

            // Header: Device Status
            HeaderSection()

            Spacer(modifier = Modifier.height(AppSpacing.L))

            when (val netState = networkState) {
                is NetworkState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangePrimary)
                    }
                }

                is NetworkState.Success -> {
                    val monitor = netState.monitor
                    val isOnline =
                        (configState as? SystemConfigState.Success)?.config?.isOnline ?: false

                    // 1. Large Connection Status Card
                    ConnectionStatusLargeCard(isOnline)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 2. Device Information Card
                    DeviceInformationCard(monitor)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 3. Signal Strength Card
                    SignalStrengthCard(
                        signalPct = monitor.wifiSignalPct,
                        rssi = monitor.wifiRssiDbm,
                        strength = monitor.wifiStrength
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 4. Hardware Health Card
                    HardwareHealthCard(monitor)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 5. Sensor Status Card
                    SensorStatusCard(monitor)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 6. Last Sync Card
                    LastSyncCard(monitor.lastUpdated)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    // 7. Database Status Card
                    DatabaseStatusCard(monitor.dbStatus)

                    Spacer(modifier = Modifier.height(AppSpacing.XL))
                }

                is NetworkState.Error -> {
                    Text(
                        text = netState.message.asString(),
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {}
            }
        }
    }
}
