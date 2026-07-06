package com.example.smarttrafficradar.features.history.presentation.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14

@Composable
fun HistoryScreen(
    viewModel: ParkingHistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val histories by viewModel.filteredHistories.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
    ) {
        HistoryHeader(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading && histories.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SmartBlue
                )
            } else if (histories.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.empty_parking_history),
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray,
                    style = MaterialTheme.typography.s14
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimen.PaddingM)
                ) {
                    items(histories, key = { it.id }) { history ->
                        HistoryCard(
                            history = history,
                            onClick = {
                                val intent = Intent(context, HistoryDetailActivity::class.java)
                                    .putExtra("historyId", history.id)
                                    .putExtra("vehicleType", history.vehicleType)

                                context.startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.M))
                    }
                }
            }
        }
    }
}