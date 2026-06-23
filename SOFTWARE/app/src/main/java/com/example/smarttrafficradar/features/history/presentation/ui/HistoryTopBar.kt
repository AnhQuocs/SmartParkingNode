package com.example.smarttrafficradar.features.history.presentation.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.util.toCurrencyFormat
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

data class HistoryInfo(
    val icon: ImageVector,
    val text: Int,
    val contentColor: Color,
    val bgrColor: Color,
    val fee: Int? = 0
)

@Composable
fun HistoryTopBar(
    history: ParkingHistory,
    onBack: () -> Unit = {}
) {
    val isParking = history.checkOutTime == null

    val historyInfo = if (isParking) {
        HistoryInfo(
            icon = Icons.Default.HourglassEmpty,
            text = R.string.parking,
            contentColor = Color.Gray,
            bgrColor = SlateMist,
        )
    } else {
        HistoryInfo(
            icon = Icons.Default.CheckCircleOutline,
            text = R.string.completed,
            contentColor = Color(0xFF008236),
            bgrColor = Color(0xFFF0FDF4),
            fee = history.fee
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .clip(
                shape = RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2,
                    bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(color = SmartBlue),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingML)
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingSM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(Dimen.SizeM)
                        .clickable { onBack() }
                )

                Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

                Text(
                    text = stringResource(id = R.string.session_details),
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(Dimen.PaddingS)
                    .clip(RoundedCornerShape(AppShape.ShapeXL))
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimen.PaddingM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(Dimen.SizeXXLPlus)
                            .clip(CircleShape)
                            .background(color = historyInfo.bgrColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = historyInfo.icon,
                            contentDescription = null,
                            tint = historyInfo.contentColor,
                            modifier = Modifier.size(Dimen.SizeXLPlus)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.XLPlus))

                    Box(
                        modifier = Modifier
                            .height(22.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeL))
                            .background(color = historyInfo.bgrColor)
                    ) {
                        Text(
                            text = stringResource(id = historyInfo.text),
                            color = historyInfo.contentColor,
                            lineHeight = 12.sp,
                            style = MaterialTheme.typography.s14,
                            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Text(
                        text = stringResource(id = R.string.parking_session_id),
                        style = MaterialTheme.typography.s16,
                        color = SlateGray
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    Text(
                        text = history.id.take(12).uppercase(),
                        style = MaterialTheme.typography.s18.semiBold(),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.XLPlus))

                    HorizontalDivider(color = SlateMist.copy(alpha = 0.6f), modifier = Modifier.height(0.5.dp))

                    Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_fee),
                            style = MaterialTheme.typography.s16,
                            color = SlateGray
                        )

                        Text(
                            text = if (isParking)
                                stringResource(id = R.string.parking)
                            else
                                (historyInfo.fee?.toCurrencyFormat() + "₫"),
                            style = MaterialTheme.typography.s18,
                            fontWeight = if (isParking) FontWeight.Medium else FontWeight.Bold,
                            color = if (isParking) SlateGray else GreenBright
                        )
                    }
                }
            }
        }
    }
}