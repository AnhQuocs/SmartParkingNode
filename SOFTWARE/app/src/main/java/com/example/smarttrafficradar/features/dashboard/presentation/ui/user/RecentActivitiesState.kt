package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.util.toCurrencyFormat
import com.example.smarttrafficradar.features.dashboard.presentation.util.toDateString
import com.example.smarttrafficradar.features.dashboard.presentation.util.toTimeString
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.presentation.viewmodel.ParkingHistoryState
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Charcoal
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.OceanBlue
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.RoyalPurpleLight
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold
import java.time.Instant
import java.time.ZoneId

@Composable
fun RecentActivitiesState(
    state: ParkingHistoryState, onDetail: (String) -> Unit, onSeeAll: () -> Unit
) {
    when (state) {
        is ParkingHistoryState.Idle, is ParkingHistoryState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LightPrimary)
            }
        }

        is ParkingHistoryState.Success -> {
            val histories = state.histories

            RecentActivitiesSection(
                histories = histories, onDetail = onDetail, onSeeAll = onSeeAll
            )
        }

        is ParkingHistoryState.Error -> {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.uiText.asString(),
                    color = Color.Red,
                    style = MaterialTheme.typography.s15
                )
            }
        }
    }
}

@Composable
fun RecentActivitiesSection(
    histories: List<ParkingHistory>, onDetail: (String) -> Unit, onSeeAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.recent_activities),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAll() }) {
                Text(
                    text = stringResource(id = R.string.see_all),
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = OceanBlue
                )

                Spacer(modifier = Modifier.width(AppSpacing.XS))

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = OceanBlue,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        if (histories.isEmpty()) {
            Text(
                text = stringResource(id = R.string.empty_parking_history),
                style = MaterialTheme.typography.s16,
                color = Charcoal,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            histories.take(3).forEach { history ->

                RecentActivitiesItem(
                    history = history,
                    onClick = { onDetail(history.id) }
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))
            }
        }
    }
}

@Composable
fun RecentActivitiesItem(
    history: ParkingHistory,
    onClick: () -> Unit
) {
    val date = history.updatedAt.toDateString()

    val checkInTime = history.checkInTime.toTimeString()
    val checkOutTime = history.checkOutTime?.toTimeString() ?: ""

    val isParking = history.checkOutTime == null

    val isOvernight = isOvernight(
        history.checkInTime,
        history.checkOutTime
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() })
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.entry_gate),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Text(
                    text = date,
                    style = MaterialTheme.typography.s15,
                    color = Charcoal,
                )

                Text(
                    text = if (isParking) stringResource(id = R.string.parking) else stringResource(
                        id = R.string.exit_gate
                    ),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = checkInTime,
                    style = MaterialTheme.typography.s12,
                    color = SlateGray
                )

                if(isOvernight) {
                    Text(
                        text = "\uD83C\uDF19 " + stringResource(id = R.string.overnight),
                        style = MaterialTheme.typography.s13,
                        color = SlateGray
                    )
                }

                Text(
                    text = if (isParking) "--:--" else checkOutTime,
                    style = MaterialTheme.typography.s12,
                    color = SlateGray
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXLPlus)
                        .clip(CircleShape)
                        .background(color = RoyalPurpleLight), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = null,
                        tint = RoyalPurple,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }

                if (isParking) {
                    Icon(
                        imageVector = Icons.Default.HourglassEmpty,
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                } else {
                    Text(
                        text = history.fee.toCurrencyFormat() + " ₫",
                        style = MaterialTheme.typography.s15.semiBold(),
                        color = GreenBright
                    )
                }

                if (!isParking) {
                    Box(
                        modifier = Modifier
                            .size(Dimen.SizeXLPlus)
                            .clip(CircleShape)
                            .background(color = TealGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = GreenBright,
                            modifier = Modifier.size(Dimen.SizeM)
                        )
                    }
                }
            }
        }
    }
}

fun isOvernight(
    checkInTime: Long,
    checkOutTime: Long?
): Boolean {
    if (checkOutTime == null) return false

    val zoneId = ZoneId.systemDefault()

    val checkInDate = Instant.ofEpochMilli(checkInTime)
        .atZone(zoneId)
        .toLocalDate()

    val checkOutDate = Instant.ofEpochMilli(checkOutTime)
        .atZone(zoneId)
        .toLocalDate()

    return checkInDate != checkOutDate
}