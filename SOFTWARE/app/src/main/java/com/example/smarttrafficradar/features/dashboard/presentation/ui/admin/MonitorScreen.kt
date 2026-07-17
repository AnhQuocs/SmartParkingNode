package com.example.smarttrafficradar.features.dashboard.presentation.ui.admin

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.dashboard.presentation.viewmodel.MonitorViewModel
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.profile.presentation.ui.admin.AdminProfileActivity
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonitorScreen(
    user: AuthUser,
    viewModel: MonitorViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val recentHistories by viewModel.recentHistories.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MonitorTopBar(
                user = user,
                onLogout = onLogout,
                onClick = {
                    context.startActivity(Intent(context, AdminProfileActivity::class.java))
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = stringResource(id = R.string.system_overview),
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = LightOnSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                // Hàng 1: Card "Xe đang trong bãi" tích hợp biểu đồ xu hướng
                SummaryCard(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.vehicles_in_lot),
                    value = "${summary.current.vehiclesInLot}",
                    subValue = "",
                    icon = Icons.Default.DirectionsCar,
                    iconColor = Color(0xFF0092CE),
                    iconBg = Color(0xFFC1E5F3).copy(alpha = 0.3f),
                    chartData = listOf(
                        15f,
                        22f,
                        18f,
                        30f,
                        25f,
                        35f,
                        summary.current.vehiclesInLot.toFloat()
                    )
                )

                // Hiển thị lưu lượng chi tiết theo loại xe
                if (summary.current.vehicleTypes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppSpacing.M))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        summary.current.vehicleTypes.forEach { (type, count) ->
                            VehicleTypeItem(
                                type = type,
                                count = count,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.M))

                // Hàng 2: "Lưu lượng" và "Doanh thu"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.today_traffic),
                        value = summary.current.todayInCount.toString(),
                        subValue = stringResource(
                            id = R.string.traffic_in_out_format,
                            summary.current.todayInCount,
                            summary.current.todayOutCount
                        ),
                        icon = Icons.Default.TrendingUp,
                        iconColor = Color(0xFF6366F1),
                        iconBg = Color(0xFFF4F3FF)
                    )

                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.today_revenue_label),
                        value = formatCurrencyShort(summary.current.todayRevenue),
                        subValue = stringResource(
                            id = R.string.revenue_comparison_format,
                            summary.revenueChangePercentage
                        ),
                        icon = Icons.Default.AccountBalanceWallet,
                        iconColor = Color(0xFF008A45),
                        iconBg = Color(0xFFF0FDF4),
                        subValueColor = if (summary.revenueChangePercentage >= 0) Color(0xFF008A45) else Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    text = stringResource(id = R.string.recent_activities),
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = LightOnSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                RecentActivitySection(recentHistories)

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }
        }
    }
}

@Composable
fun VehicleTypeItem(
    type: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    val (labelRes, icon) = when (type.lowercase()) {
        "car" -> R.string.car to Icons.Default.DirectionsCar
        "motorbike" -> R.string.motorcycle to Icons.Default.TwoWheeler
        else -> R.string.vehicle_type to Icons.Default.DirectionsCar
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeM),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimen.SizeS)
            )
            Spacer(modifier = Modifier.width(AppSpacing.XS))
            Text(
                text = stringResource(
                    id = R.string.vehicle_type_count_format,
                    stringResource(id = labelRes),
                    count
                ),
                style = MaterialTheme.typography.s12.semiBold(),
                color = LightOnSurface
            )
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    iconColor: Color,
    iconBg: Color,
    modifier: Modifier = Modifier,
    subValueColor: Color = SlateGray,
    chartData: List<Float>? = null
) {
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeXL),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXLPlus)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.M))

                Text(
                    text = value,
                    style = MaterialTheme.typography.s24.semiBold(),
                    color = LightOnSurface
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )

                if (subValue.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(AppSpacing.XXS))
                    Text(
                        text = subValue,
                        style = MaterialTheme.typography.s12,
                        color = subValueColor
                    )
                }
            }

            if (chartData != null) {
                Spacer(modifier = Modifier.width(AppSpacing.M))
                SparklineChart(
                    data = chartData,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(80.dp)
                        .padding(vertical = AppSpacing.S),
                    color = iconColor
                )
            }
        }
    }
}

@Composable
fun SparklineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = Color.Blue
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas
        val width = size.width
        val height = size.height
        val max = data.maxOrNull() ?: 1f
        val min = data.minOrNull() ?: 0f
        val range = (max - min).coerceAtLeast(1f)

        val path = Path()
        data.forEachIndexed { index, value ->
            val x = index * (width / (data.size - 1))
            val y = height - ((value - min) / range * height)
            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Vẽ vùng gradient phía dưới đường line
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )
    }
}

@Composable
fun RecentActivitySection(histories: List<ParkingHistory>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(bottom = Dimen.PaddingM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeXL),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = Dimen.PaddingM)) {
            if (histories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingL),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.empty_parking_history),
                        style = MaterialTheme.typography.s12,
                        color = SlateGray
                    )
                }
            } else {
                histories.forEachIndexed { index, history ->
                    key(history.id) {
                        val visibleState = remember {
                            MutableTransitionState(false).apply { targetState = true }
                        }

                        AnimatedVisibility(
                            visibleState = visibleState,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                RecentActivityItem(history)
                                if (index < histories.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF3F4F6))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecentActivityItem(history: ParkingHistory) {
    val checkInTimeText = formatTime(history.checkInTime)
    val checkOutTimeText = history.checkOutTime?.let { formatTime(it) }
    val isStillParking = history.checkOutTime == null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.M),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- START: Icon và Giờ vào ---
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF008A45).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Login,
                    contentDescription = null,
                    tint = Color(0xFF008A45),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Column {
                Text(
                    text = stringResource(R.string.in_label),
                    style = MaterialTheme.typography.s12,
                    color = Color(0xFF008A45)
                )
                Text(
                    text = checkInTimeText,
                    style = MaterialTheme.typography.s12.semiBold(),
                    color = LightOnSurface
                )
            }
        }

        // --- CENTER: RFID ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = AppSpacing.S)
        ) {
            Text(
                text = stringResource(R.string.card_label),
                style = MaterialTheme.typography.s12,
                color = SlateGray
            )
            Text(
                text = history.rfidUid,
                style = MaterialTheme.typography.s14.semiBold(),
                color = LightOnSurface
            )
        }

        // --- END: Exit Info ---
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.out_label),
                    style = MaterialTheme.typography.s12,
                    color = if (!isStillParking) Color.Red else SlateGray
                )
                Text(
                    text = checkOutTimeText ?: stringResource(R.string.parking),
                    style = MaterialTheme.typography.s12.semiBold(),
                    color = if (!isStillParking) LightOnSurface else SlateGray
                )
            }
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (!isStillParking) Color.Red.copy(alpha = 0.1f)
                        else SlateGray.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = if (!isStillParking) Color.Red else SlateGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatCurrencyShort(amount: Long): String {
    return if (amount >= 1_000_000) {
        String.format("%.2fMđ", amount.toDouble() / 1_000_000)
    } else if (amount >= 1_000) {
        String.format("%.1fkđ", amount.toDouble() / 1_000)
    } else {
        String.format("%dđ", amount)
    }
}

private fun formatTime(timestamp: Long?): String {
    if (timestamp == null || timestamp == 0L) return ""
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}