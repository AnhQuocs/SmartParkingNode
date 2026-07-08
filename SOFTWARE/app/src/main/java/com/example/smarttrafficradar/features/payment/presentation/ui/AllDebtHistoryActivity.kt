package com.example.smarttrafficradar.features.payment.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.presentation.ui.HistoryDetailActivity
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryState
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllDebtHistoryActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = intent.getStringExtra("uid") ?: ""

        setContent {
            val historyViewModel: ParkingHistoryViewModel = hiltViewModel()

            AllDebtHistoryScreen(
                uid = uid,
                onBacK = { finish() },
                viewModel = historyViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDebtHistoryScreen(
    uid: String,
    onBacK: () -> Unit,
    viewModel: ParkingHistoryViewModel
) {
    val context = LocalContext.current
    val historyState by viewModel.historyState.collectAsState()

    LaunchedEffect(uid) {
        viewModel.observeHistories(uid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.debt_history),
                        style = MaterialTheme.typography.s16.semiBold(),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBacK) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            when (val state = historyState) {
                is ParkingHistoryState.Idle, ParkingHistoryState.Loading -> {
                    CircularProgressIndicator(color = LightPrimary)
                }

                is ParkingHistoryState.Success -> {
                    val histories = state.histories
                    val debtHistories = histories.filter { history -> history.fee != 0 }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Dimen.PaddingM),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                    ) {
                        if (debtHistories.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.no_outstanding_debt_history),
                                        style = MaterialTheme.typography.s16.semiBold(),
                                        color = SlateGray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            items(
                                items = debtHistories,
                                key = { it.id }
                            ) { history ->
                                DebtHistoryItem(
                                    history = history,
                                    onClick = { historyId ->
                                        val intent =
                                            Intent(context, HistoryDetailActivity::class.java)
                                                .putExtra("historyId", historyId)

                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }

                is ParkingHistoryState.Error -> {
                    Text(
                        text = state.uiText.asString(),
                        style = MaterialTheme.typography.s15,
                        color = ActionDanger
                    )
                }
            }
        }
    }
}