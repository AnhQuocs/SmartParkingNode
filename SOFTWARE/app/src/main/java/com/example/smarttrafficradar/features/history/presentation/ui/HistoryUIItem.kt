package com.example.smarttrafficradar.features.history.presentation.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryHeader(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2,
                    bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(SmartBlue)
            .padding(bottom = Dimen.PaddingXL)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingXL)
                .padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(id = R.string.history_title),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.search_by_session_id),
                        color = Color.Gray,
                        style = MaterialTheme.typography.s14
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun HistoryCard(history: ParkingHistory, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingM)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(AppShape.ShapeM))
                        .background(SmartBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (history.vehicleType == VehicleType.CAR) Icons.Default.DirectionsCar else Icons.Default.DirectionsBike,
                        contentDescription = null,
                        tint = SmartBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (history.vehicleType == VehicleType.CAR) stringResource(id = R.string.car) else stringResource(
                            id = R.string.motorcycle
                        ),
                        style = MaterialTheme.typography.s18.semiBold(),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.XXS))

                    Text(
                        text = history.id.take(12).uppercase(),
                        style = MaterialTheme.typography.s14,
                        color = SlateGray
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = SlateMist,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            HistoryInfoRow(
                label = stringResource(id = R.string.check_in_time_label),
                value = formatTimestamp(history.checkInTime)
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            HistoryInfoRow(
                label = stringResource(id = R.string.check_out_time_label),
                value = history.checkOutTime?.let { formatTimestamp(it) } ?: "---"
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            HistoryInfoRow(
                label = stringResource(id = R.string.parking_duration),
                value = context.formatDuration(history.durationMinutes)
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Text(
                text = "${formatFee(history.fee)} đ",
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color(0xFF2E7D32),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun HistoryInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.s14,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.s14.semiBold(),
            color = Color.Black
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "---"
    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun Context.formatDuration(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60

    return if (h > 0) {
        getString(R.string.duration_hours_minutes, h, m)
    } else {
        getString(R.string.duration_minutes_value, m)
    }
}

private fun formatFee(fee: Int): String {
    return String.format("%,d", fee).replace(',', '.')
}
