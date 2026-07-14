package com.example.smarttrafficradar.features.analytics.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.analytics.presentation.viewmodel.Period
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightOnBackground
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.utils.s10
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodSelected: (Period) -> Unit,
    onCustomClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        val periods = listOf(
            Period.DAILY to stringResource(id = R.string.day),
            Period.WEEKLY to stringResource(id = R.string.week),
            Period.MONTHLY to stringResource(id = R.string.month)
        )

        periods.forEach { (period, label) ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LightPrimary,
                    selectedLabelColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedPeriod == period,
                    borderColor = SlateGray.copy(alpha = 0.5f)
                )
            )
        }

        FilterChip(
            selected = selectedPeriod == Period.CUSTOM,
            onClick = onCustomClick,
            label = { Text(stringResource(id = R.string.calendar)) },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, null, Modifier.size(Dimen.SizeS)) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LightPrimary,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
            )
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(AppShape.ShapeL),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Text(
                title,
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.s12
            )
            Text(value, color = Color.White, style = MaterialTheme.typography.s18.semiBold())
        }
    }
}

@Composable
fun TransactionItem(transaction: PaymentHistory) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeM),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(id = R.string.momo_transaction),
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = LightOnBackground
                )
                Text(
                    text = sdf.format(Date(transaction.createdAt)),
                    style = MaterialTheme.typography.s12,
                    color = SlateGray
                )
            }
            Text(
                text = "+${formatCurrency(transaction.amount.toLong())}",
                color = SuccessGreen,
                style = MaterialTheme.typography.s16.semiBold()
            )
        }
    }
}

@Composable
fun SimpleRevenueChart(
    transactions: List<PaymentHistory>,
    period: Period,
    modifier: Modifier = Modifier
) {
    val chartData = remember(transactions, period) {
        val data = mutableListOf<Pair<String, Long>>()

        when (period) {
            Period.DAILY -> {
                val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
                for (i in 6 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -i)
                    val label = sdf.format(cal.time)
                    val amount = transactions.filter { t ->
                        val tCal = Calendar.getInstance().apply { timeInMillis = t.createdAt }
                        tCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                                tCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
                    }.sumOf { it.amount.toLong() }
                    data.add(label to amount)
                }
            }

            Period.WEEKLY -> {
                for (i in 3 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.WEEK_OF_YEAR, -i)
                    val label = "T.${cal.get(Calendar.WEEK_OF_YEAR)}"
                    val amount = transactions.filter { t ->
                        val tCal = Calendar.getInstance().apply { timeInMillis = t.createdAt }
                        tCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                                tCal.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR)
                    }.sumOf { it.amount.toLong() }
                    data.add(label to amount)
                }
            }

            Period.MONTHLY -> {
                val sdf = SimpleDateFormat("MM/yy", Locale.getDefault())
                for (i in 11 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.MONTH, -i)
                    val label = sdf.format(cal.time)
                    val amount = transactions.filter { t ->
                        val tCal = Calendar.getInstance().apply { timeInMillis = t.createdAt }
                        tCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
                                tCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                    }.sumOf { it.amount.toLong() }
                    data.add(label to amount)
                }
            }

            Period.CUSTOM -> {
                val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
                transactions.groupBy { sdf.format(Date(it.createdAt)) }
                    .mapValues { it.value.sumOf { t -> t.amount.toLong() } }
                    .toList()
                    .sortedBy { it.first }
                    .forEach { data.add(it) }
            }
        }
        data
    }

    val maxAmount = chartData.maxOfOrNull { it.second }?.toFloat()?.coerceAtLeast(1f) ?: 1f
    val scrollState = rememberScrollState()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeL),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.M),
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach { (label, amount) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = if (amount >= 1000) "${amount / 1000}k" else amount.toString(),
                            style = MaterialTheme.typography.s10.semiBold(),
                            color = LightPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.XXS))
                        val barHeight = (amount.toFloat() / maxAmount) * 150
                        Box(
                            modifier = Modifier
                                .width(if (period == Period.MONTHLY) 36.dp else 44.dp)
                                .height(barHeight.dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (amount > 0) LightPrimary else SlateGray.copy(alpha = 0.1f))
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.S))
                        Text(
                            label,
                            style = MaterialTheme.typography.s10,
                            color = SlateGray,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onDateSelected: (Long?, Long?) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(
                    dateRangePickerState.selectedStartDateMillis,
                    dateRangePickerState.selectedEndDateMillis
                )
            }) {
                Text(stringResource(id = R.string.select))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    stringResource(id = R.string.select_date_range),
                    modifier = Modifier.padding(Dimen.PaddingM)
                )
            }
        )
    }
}

fun formatCurrency(amount: Long): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(amount)
}
