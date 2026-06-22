package com.example.smarttrafficradar.features.history.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingStatus
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.ui.theme.SmartTrafficRadarTheme
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s18

data class HistoryInfo(
    val icon: ImageVector,
    val text: Int,
    val contentColor: Color,
    val bgrColor: Color,
    val fee: Int? = 0
)

@Preview(showBackground = true)
@Composable
fun HistoryTopBar(
//    history: ParkingHistory,
    onBack: () -> Unit = {}
) {
    val mockParkingHistory = ParkingHistory(
        id = "PH001",
        userId = "USER001",
        rfidUid = "A1B2C3D4",
        checkInTime = System.currentTimeMillis() - 2 * 60 * 60 * 1000, // 2 giờ trước
        checkOutTime = System.currentTimeMillis(),
        durationMinutes = 120,
        fee = 10000,
        status = ParkingStatus.CHECK_OUT,
        createdAt = System.currentTimeMillis() - 2 * 60 * 60 * 1000,
        updatedAt = System.currentTimeMillis()
    )

    val isParking = mockParkingHistory.checkOutTime == null

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
            fee = mockParkingHistory.fee
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
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
                modifier = Modifier.fillMaxWidth(),
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
                    style = MaterialTheme.typography.s18,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(Dimen.PaddingM)
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

                    Spacer(modifier = Modifier.height(AppSpacing.XXL))

                    Box(
                        modifier = Modifier
                            .height(22.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeL))
                            .background(color = historyInfo.bgrColor)
                    ) {
                        Text(
                            text = stringResource(id = historyInfo.text),
                            color = historyInfo.contentColor,
                            style = MaterialTheme.typography.s14
                        )
                    }
                }
            }
        }
    }
}