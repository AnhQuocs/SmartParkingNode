package com.example.smarttrafficradar.features.payment.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.util.toDateString
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.OceanBlue
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun DebtHistorySection(
    onSeeAll: () -> Unit,
    onDetail: (String) -> Unit,
    debtHistories: List<ParkingHistory>?
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
                text = stringResource(id = R.string.debt_history),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                modifier = Modifier.clickable { onSeeAll() }) {
                Text(
                    text = stringResource(id = R.string.see_all),
                    style = MaterialTheme.typography.s14,
                    color = LightPrimary
                )

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = LightPrimary,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }
        }

        debtHistories?.take(4)?.forEach { history ->
            DebtHistoryItem(history = history, onClick = onDetail)
        }
    }
}

@Composable
fun DebtHistoryItem(
    history: ParkingHistory,
    onClick: (String) -> Unit,
) {
    val isOverNight = history.notifiedNights != 0
    val checkInDate = history.checkInTime.toDateString()

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick(history.id) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = Dimen.PaddingS)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(OceanBlue.copy(alpha = 0.4f)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = checkInDate,
                    style = MaterialTheme.typography.s16,
                    color = Color.Black,
                    modifier = Modifier.padding(start = Dimen.PaddingM)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
            ) {
                Text(
                    text = stringResource(id = if (!isOverNight) R.string.parking_internal_motorbike_day else R.string.parking_internal_motorbike_overnight),
                    style = MaterialTheme.typography.s16,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.debt_increased),
                        style = MaterialTheme.typography.s16,
                        color = SlateGray
                    )

                    Text(
                        text = formatFee(history.fee) + " đ",
                        style = MaterialTheme.typography.s15,
                        color = GreenBright
                    )
                }
            }
        }
    }
}

private fun formatFee(fee: Int): String {
    return String.format("%,d", fee).replace(',', '.')
}