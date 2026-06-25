package com.example.smarttrafficradar.features.history.presentation.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.util.toDateString
import com.example.smarttrafficradar.features.dashboard.presentation.util.toTimeString
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark

@Composable
fun TimeDetailsCard(
    history: ParkingHistory,
    modifier: Modifier = Modifier
) {
    val isParking = history.checkOutTime == null

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeXL2),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.PaddingL)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.time_details_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            TimeStepItem(
                label = stringResource(R.string.entry_gate),
                time = history.checkInTime,
                iconColor = RoyalBlue,
                iconBgColor = RoyalBlueLight,
                showDashedLine = true
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = Dimen.PaddingL, bottom = Dimen.PaddingL)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(6.dp)
                            .background(Color.LightGray)
                    )
                }
            }

            TimeStepItem(
                label = stringResource(R.string.exit_gate),
                time = history.checkOutTime,
                iconColor = TealGreen,
                iconBgColor = TealGreenLight,
                isParking = isParking
            )
        }
    }
}

@Composable
private fun TimeStepItem(
    label: String,
    time: Long?,
    iconColor: Color,
    iconBgColor: Color,
    isParking: Boolean = false,
    showDashedLine: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(Dimen.SizeXXL)
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXLPlus)
                    .background(color = iconBgColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeSM),
                    tint = iconColor
                )
            }
        }

        Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

        Column(modifier = Modifier.padding(bottom = if (showDashedLine) 0.dp else 0.dp)) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clock),
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeS),
                    tint = SlateGray
                )
                Spacer(modifier = Modifier.width(AppSpacing.S))
                Text(
                    text = if (isParking) {
                        stringResource(R.string.parking)
                    } else if (time != null) {
                        stringResource(
                            R.string.date_time_format,
                            time.toTimeString(),
                            time.toDateString()
                        )
                    } else "--:--",
                    fontSize = 14.sp,
                    color = if (isParking) RoyalBlue else SlateGray,
                    fontWeight = if (isParking) FontWeight.Medium else FontWeight.Normal
                )
            }

            // Tạo khoảng trống nếu có đường kẻ để căn chỉnh với icon tiếp theo
            if (showDashedLine) {
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }
        }
    }
}