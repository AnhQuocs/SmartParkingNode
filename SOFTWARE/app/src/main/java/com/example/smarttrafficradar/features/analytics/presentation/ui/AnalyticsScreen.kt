package com.example.smarttrafficradar.features.analytics.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.analytics.presentation.util.ReportExporter
import com.example.smarttrafficradar.features.analytics.presentation.viewmodel.AnalyticsViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightOnBackground
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.revenue_statistics),
                        style = MaterialTheme.typography.s18.semiBold(),
                        color = LightOnBackground
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                ),
                actions = {
                    IconButton(onClick = {
                        ReportExporter.exportTransactionsToCsv(context, state.filteredTransactions)
                    }) {
                        Icon(
                            Icons.Default.FileDownload,
                            contentDescription = stringResource(id = R.string.export),
                            tint = LightPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = Dimen.PaddingM)
        ) {
            item {
                PeriodSelector(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodSelected = { viewModel.setPeriod(it) },
                    onCustomClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    StatCard(
                        title = stringResource(id = R.string.total_revenue),
                        value = formatCurrency(state.totalRevenue),
                        icon = Icons.Default.Payments,
                        containerColor = LightPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(id = R.string.transactions),
                        value = state.filteredTransactions.size.toString(),
                        icon = Icons.Default.CalendarMonth,
                        containerColor = RoyalBlue,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    stringResource(id = R.string.revenue_chart),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = LightOnBackground
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))

                SimpleRevenueChart(
                    transactions = state.filteredTransactions,
                    period = state.selectedPeriod,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    stringResource(id = R.string.transaction_details),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = LightOnBackground
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            items(state.filteredTransactions) { transaction ->
                TransactionItem(transaction)
                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            item {
                Spacer(modifier = Modifier.height(Dimen.PaddingM))
            }
        }

        if (showDatePicker) {
            DateRangePickerModal(
                onDismiss = { showDatePicker = false },
                onDateSelected = { start, end ->
                    viewModel.setCustomRange(start, end)
                    showDatePicker = false
                }
            )
        }
    }
}
