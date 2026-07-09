package com.example.smarttrafficradar.features.payment.presentation.ui.pay_history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AppVersionText
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightError
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SuccessBackground
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PayHistoryItem(
    history: PaymentHistory,
    modifier: Modifier = Modifier
) {
    val isSuccess = history.status == "SUCCESS"
    val dateString = formatDate(history.createdAt)
    val timeString = formatTime(history.createdAt)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.S),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.PaddingM)
        ) {
            // Header: Icon + Title + ID
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXLPlus)
                        .clip(RoundedCornerShape(AppShape.ShapeM))
                        .background(if (isSuccess) SuccessBackground else LightError.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircleOutline else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = if (isSuccess) SuccessGreen else LightError,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.M))

                Column {
                    Text(
                        text = stringResource(id = R.string.payment_history_item_title, dateString),
                        style = MaterialTheme.typography.s16.semiBold(),
                        color = TextPrimaryDark
                    )
                    Text(
                        text = history.id.take(12).uppercase(),
                        style = MaterialTheme.typography.s12,
                        color = AppVersionText
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            // Info rows
            InfoRow(
                label = stringResource(id = R.string.payment_history_method_label),
                value = if (history.method == "MOMO") stringResource(id = R.string.payment_history_method_momo) else history.method,
                icon = Icons.Default.AccountBalanceWallet
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            InfoRow(
                label = stringResource(id = R.string.payment_history_time_label),
                value = "$timeString - $dateString",
                icon = Icons.Default.AccessTime
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            // Divider
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Background
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            // Bottom: Status Badge + Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Badge
                Surface(
                    color = if (isSuccess) SuccessBackground else LightError.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(AppShape.ShapeS)
                ) {
                    Text(
                        text = if (isSuccess) stringResource(id = R.string.payment_history_status_success) 
                               else stringResource(id = R.string.payment_history_status_failed),
                        modifier = Modifier.padding(horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXS),
                        style = MaterialTheme.typography.s12.semiBold(),
                        color = if (isSuccess) SuccessGreen else LightError
                    )
                }

                // Amount
                Text(
                    text = stringResource(id = R.string.currency_vnd_suffix, formatAmount(history.amount)),
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = SuccessGreen
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.s14,
            color = SlateGray,
            modifier = Modifier.weight(1f)
        )
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(Dimen.SizeS)
            )
            Spacer(modifier = Modifier.width(AppSpacing.XSPlus))
            Text(
                text = value,
                style = MaterialTheme.typography.s14.semiBold(),
                color = TextPrimaryDark
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatAmount(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
