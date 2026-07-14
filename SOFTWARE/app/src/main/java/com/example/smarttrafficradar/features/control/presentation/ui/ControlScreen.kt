package com.example.smarttrafficradar.features.control.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.app_system.language.presentation.ui.ChangeLanguageActivity
import com.example.smarttrafficradar.features.app_system.language.presentation.viewmodel.LanguageViewModel
import com.example.smarttrafficradar.features.control.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.control.presentation.viewmodel.ControlViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AmberOrange
import com.example.smarttrafficradar.ui.theme.AmberOrangeLight
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.LightError
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.NatureBackground
import com.example.smarttrafficradar.ui.theme.OceanBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.RoyalPurpleLight
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import java.util.Locale

@Composable
fun ControlScreen(
    languageViewModel: LanguageViewModel = hiltViewModel(),
    controlViewModel: ControlViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentLang by languageViewModel.currentLanguage.collectAsState()
    val selectedLang by remember(currentLang) { mutableStateOf(currentLang) }

    val systemMonitor by controlViewModel.systemMonitorState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        ControlTopBar()

        Column(
            modifier = Modifier.padding(horizontal = Dimen.PaddingM)
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            SystemMonitorGrid(systemMonitor = systemMonitor)

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            LanguagesCard(
                selectedLang = selectedLang,
                onChangeLanguage = {
                    val intent = Intent(context, ChangeLanguageActivity::class.java)
                    context.startActivity(intent)
                }
            )

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
        }
    }
}

@Composable
fun SystemMonitorGrid(systemMonitor: SystemMonitor) {
    val node = systemMonitor.parkingNode

    // Section 1: ESP32 Health
    MonitorCard(title = stringResource(R.string.esp32_overview)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Thermostat,
                    label = stringResource(R.string.avg_temp),
                    value = "${String.format(Locale.ROOT, "%.1f", node.cpuTempC)}°C",
                    iconColor = RoyalBlue,
                    backgroundColor = RoyalBlueLight
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Memory,
                    label = stringResource(R.string.avg_cpu),
                    value = "${node.cpuUsagePct}%",
                    iconColor = TealGreen,
                    backgroundColor = TealGreenLight
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Row(modifier = Modifier.fillMaxWidth()) {
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DataUsage,
                    label = stringResource(R.string.heap_memory),
                    value = "${String.format(Locale.ROOT, "%.1f", node.heapUsagePct)}%",
                    iconColor = OceanBlue,
                    backgroundColor = BabyBlue
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bolt,
                    label = stringResource(R.string.power_source),
                    value = node.powerStatus.ifEmpty { "---" },
                    iconColor = RoyalPurple,
                    backgroundColor = RoyalPurpleLight
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.M))

    // Section 2: Network Details
    MonitorCard(title = stringResource(R.string.network_details)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonitorGridItem(
                    modifier = Modifier.weight(1.5f),
                    icon = Icons.Default.Lan,
                    label = stringResource(R.string.ip_addr),
                    value = node.connectionStatus.ipAddress.ifEmpty { "---" },
                    iconColor = Color(0xFF455A64),
                    backgroundColor = Color(0xFFECEFF1)
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.NetworkCheck,
                    label = stringResource(R.string.wifi_signal),
                    value = "${node.wifiSignalPct}%",
                    iconColor = GreenBright,
                    backgroundColor = NatureBackground
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Row(modifier = Modifier.fillMaxWidth()) {
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Wifi,
                    label = stringResource(R.string.wifi),
                    value = "${node.wifiRssiDbm} dBm",
                    iconColor = OceanBlue,
                    backgroundColor = RoyalBlueLight
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                MonitorGridItem(
                    modifier = Modifier.weight(1.2f),
                    icon = Icons.Default.CloudQueue,
                    label = stringResource(R.string.firebase_status),
                    value = node.connectionStatus.firebaseStatus.ifEmpty { "---" },
                    iconColor = AmberOrange,
                    backgroundColor = AmberOrangeLight
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.M))

    // Section 3: Sensors & Peripheral
    MonitorCard(title = stringResource(R.string.sensors_peripheral)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Login,
                    label = stringResource(R.string.ir_in),
                    value = node.irInStatus.ifEmpty { "---" },
                    iconColor = TealGreen,
                    backgroundColor = TealGreenLight
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                MonitorGridItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Logout,
                    label = stringResource(R.string.ir_out),
                    value = node.irOutStatus.ifEmpty { "---" },
                    iconColor = LightError,
                    backgroundColor = Color(0xFFFFEBEE)
                )
            }
            Spacer(modifier = Modifier.height(AppSpacing.S))
            MonitorGridItem(
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Contactless,
                label = stringResource(R.string.rfid_sensor),
                value = node.rfidStatus.ifEmpty { "---" },
                iconColor = RoyalPurple,
                backgroundColor = RoyalPurpleLight
            )
        }
    }
}

@Composable
fun MonitorCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppShape.ShapeXL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingM)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LightOnSurface
            )
            Spacer(modifier = Modifier.height(AppSpacing.M))
            content()
        }
    }
}

@Composable
fun MonitorGridItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    backgroundColor: Color
) {
    Surface(
        modifier = modifier.height(85.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppShape.ShapeL),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM),
                tint = iconColor
            )

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray,
                    maxLines = 1
                )
            }
        }
    }
}
