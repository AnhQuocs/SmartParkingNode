package com.example.smarttrafficradar.features.system_monitor.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system_monitor.domain.model.SystemMonitor
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.NeonGreen
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s24
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HeaderSection() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E2230))
                .border(1.dp, Color(0xFF2E3544), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_radio_signal),
                contentDescription = null,
                tint = Color(0xFF818CF8),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = stringResource(R.string.device_status),
                style = MaterialTheme.typography.s24.bold(),
                color = Color.White
            )
            Text(
                text = stringResource(R.string.hardware_monitoring),
                style = MaterialTheme.typography.s14,
                color = SlateMist
            )
        }
    }
}

@Composable
fun ConnectionStatusLargeCard(isOnline: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
        border = BorderStroke(
            1.dp, if (isOnline) NeonGreen.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isOnline) NeonGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        if (isOnline) NeonGreen.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f),
                        RoundedCornerShape(20.dp)
                    ), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wifi),
                    contentDescription = null,
                    tint = if (isOnline) NeonGreen else Color.Red,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = stringResource(R.string.connection_status),
                    style = MaterialTheme.typography.s14,
                    color = Color.Gray
                )
                Text(
                    text = if (isOnline) stringResource(R.string.online) else stringResource(R.string.offline),
                    style = MaterialTheme.typography.s24.bold(),
                    color = if (isOnline) NeonGreen else Color.Red
                )
            }
        }
    }
}

@Composable
fun DeviceInformationCard(monitor: SystemMonitor) {
    SectionCard(
        title = stringResource(R.string.device_information),
        subtitle = stringResource(R.string.hardware_details),
        iconId = R.drawable.ic_iot
    ) {
        InfoRow(stringResource(R.string.device_id), monitor.deviceId)
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp)
        )
        InfoRow(stringResource(R.string.device_type), monitor.deviceType)
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp)
        )
        InfoRow(stringResource(R.string.firmware), monitor.firmware)
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp)
        )
        InfoRow(stringResource(R.string.uptime), monitor.uptime)
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp)
        )
        InfoRow(stringResource(R.string.connected_to), monitor.wifiSsid)
    }
}

@Composable
fun SignalStrengthCard(signalPct: Int, rssi: Int, strength: String) {
    SectionCard(
        title = stringResource(R.string.signal_strength),
        subtitle = "$strength connection",
        iconId = R.drawable.ic_wifi
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$rssi dBm", style = MaterialTheme.typography.s14, color = Color.Gray
            )
            Text(
                text = "$signalPct%", style = MaterialTheme.typography.s24.bold(), color = NeonGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { signalPct / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape),
            color = NeonGreen,
            trackColor = Color.White.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4 levels of bars: 25%, 50%, 75%, 90% (adjust 4th bar to 90% so 88% only shows 3 bars)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val thresholds = listOf(0, 25, 50, 90)
            repeat(4) { index ->
                val isActive = signalPct > thresholds[index]
                Box(
                    modifier = Modifier
                        .weight(1f + index * 0.2f)
                        .height(16.dp + (index * 4).dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isActive) NeonGreen else Color.White.copy(alpha = 0.1f))
                )
            }
        }
    }
}

@Composable
fun HardwareHealthCard(monitor: SystemMonitor) {
    SectionCard(
        title = stringResource(R.string.hardware_health),
        subtitle = stringResource(R.string.system_performance),
        iconId = R.drawable.ic_cpu
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            HealthRow(
                stringResource(R.string.cpu_usage),
                "${monitor.cpuUsagePct}%",
                monitor.cpuUsagePct / 100f
            )
            HealthRow(
                stringResource(R.string.cpu_temp),
                "${monitor.cpuTempC}°C",
                (monitor.cpuTempC / 100f).toFloat()
            )
            HealthRow(
                stringResource(R.string.ram_usage),
                "${monitor.heapUsagePct.toInt()}%",
                (monitor.heapUsagePct / 100f).toFloat()
            )
            HealthRow(
                stringResource(R.string.fps), monitor.fps.toString(), (monitor.fps / 60f).toFloat()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
        Spacer(modifier = Modifier.height(20.dp))

        InfoRow(stringResource(R.string.cycle_time), "${monitor.cycleTimeMs} ms")
        Spacer(modifier = Modifier.height(8.dp))
        InfoRow(stringResource(R.string.ram_free), "${monitor.heapFreeBytes / 1024} KB")
    }
}

@Composable
fun HealthRow(label: String, value: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.s14, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.s14.bold(), color = Color.White)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape),
            color = CyanPrimary,
            trackColor = Color.White.copy(alpha = 0.05f)
        )
    }
}

@Composable
fun SensorStatusCard(monitor: SystemMonitor) {
    SectionCard(
        title = stringResource(R.string.sensor_status),
        subtitle = stringResource(R.string.peripheral_check),
        iconId = R.drawable.ic_radio_signal
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SensorRow(stringResource(R.string.ir_sensor_1), monitor.ir1Status)
            SensorRow(stringResource(R.string.ir_sensor_2), monitor.ir2Status)
            SensorRow(stringResource(R.string.rfid_module), monitor.rfidStatus)
        }
    }
}

@Composable
fun SensorRow(label: String, status: String) {
    val isOk = status.uppercase() == "OK"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.s16, color = Color.White)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isOk) NeonGreen.copy(alpha = 0.1f) else Color.Red.copy(alpha = 0.1f))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                color = if (isOk) NeonGreen else Color.Red,
                style = MaterialTheme.typography.s14.bold()
            )
        }
    }
}

@Composable
fun LastSyncCard(timestamp: Long) {
    SectionCard(
        title = stringResource(R.string.last_sync),
        subtitle = stringResource(R.string.data_synchronization),
        iconId = R.drawable.ic_clock
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.last_sync), color = Color.Gray)
            Text(
                text = formatTimestampRelative(timestamp),
                color = NeonGreen,
                style = MaterialTheme.typography.s16.bold()
            )
        }
    }
}

@Composable
fun DatabaseStatusCard(status: String) {
    SectionCard(
        title = stringResource(R.string.database_status),
        subtitle = stringResource(R.string.firebase_connection),
        iconId = R.drawable.ic_database
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            val isConnected = status.uppercase() == "CONNECTED"
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isConnected) NeonGreen.copy(alpha = 0.1f) else Color.Red.copy(
                            alpha = 0.1f
                        )
                    )
                    .border(1.dp, if (isConnected) NeonGreen else Color.Red, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = status,
                    color = if (isConnected) NeonGreen else Color.Red,
                    style = MaterialTheme.typography.s14.bold()
                )
            }
        }
    }
}

@Composable
fun SectionCard(
    title: String, subtitle: String, iconId: Int, content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingSM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.s18.bold(),
                        color = Color.White
                    )
                    Text(text = subtitle, style = MaterialTheme.typography.s14, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, style = MaterialTheme.typography.s16)
        Text(text = value, color = Color.White, style = MaterialTheme.typography.s16.bold())
    }
}

fun formatTimestampRelative(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val diff = System.currentTimeMillis() - timestamp
    return if (diff < 60_000) "Just now" else {
        val sdf = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        sdf.format(Date(timestamp))
    }
}
