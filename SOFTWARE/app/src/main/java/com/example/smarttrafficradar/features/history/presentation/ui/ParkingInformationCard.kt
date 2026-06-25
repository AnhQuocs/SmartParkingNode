package com.example.smarttrafficradar.features.history.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.RoyalPurpleLight
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark
import com.example.smarttrafficradar.ui.theme.TextSecondary

@Composable
fun ParkingInformationCard(
    vehicleType: VehicleType,
    parkingHistory: ParkingHistory,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(AppShape.ShapeXL2),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.PaddingL)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.parking_info_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            val vehicleIcon =
                if (vehicleType == VehicleType.CAR) R.drawable.ic_car else R.drawable.ic_motorcycle
            val vehicleName =
                if (vehicleType == VehicleType.CAR) stringResource(R.string.car) else stringResource(
                    R.string.motorcycle
                )

            InfoItem(
                icon = painterResource(id = vehicleIcon),
                label = stringResource(R.string.vehicle_type_label),
                value = vehicleName,
                iconColor = RoyalBlue,
                iconBgColor = RoyalBlueLight
            )

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            InfoItem(
                icon = painterResource(id = R.drawable.ic_card),
                label = stringResource(R.string.rfid_card_label),
                value = parkingHistory.rfidUid,
                iconColor = RoyalPurple,
                iconBgColor = RoyalPurpleLight
            )

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            val hours = parkingHistory.durationMinutes / 60
            val minutes = parkingHistory.durationMinutes % 60
            val durationText = if (hours > 0) {
                stringResource(R.string.duration_hours_minutes, hours, minutes)
            } else {
                stringResource(R.string.duration_minutes_only, minutes)
            }

            InfoItem(
                icon = painterResource(id = R.drawable.ic_clock),
                label = stringResource(R.string.parking_duration_label),
                value = durationText,
                iconColor = TealGreen,
                iconBgColor = TealGreenLight
            )
        }
    }
}

@Composable
private fun InfoItem(
    icon: Painter, label: String, value: String, iconColor: Color, iconBgColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(Dimen.SizeXXL)
                .background(color = iconBgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM),
                tint = iconColor
            )
        }

        Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

        Column {
            Text(
                text = label, fontSize = 13.sp, color = TextSecondary
            )
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryDark
            )
        }
    }
}