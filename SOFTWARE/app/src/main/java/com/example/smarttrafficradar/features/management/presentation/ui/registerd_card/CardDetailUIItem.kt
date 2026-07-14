package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CardInformation(
    card: RegisteredCard,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val dateTimeFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeL),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(id = R.string.card_info),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LightOnSurface,
                modifier = Modifier.padding(bottom = AppSpacing.MPlus)
            )

            InfoRow(label = stringResource(id = R.string.rfid_uid), value = card.rfidUid)
            InfoRow(label = stringResource(id = R.string.card_owner), value = card.ownerName)

            val memberTypeStr = when (card.cardType) {
                MemberType.STUDENT -> stringResource(id = R.string.student)
                MemberType.EMPLOYEE -> stringResource(id = R.string.employee)
            }
            InfoRow(
                label = stringResource(id = R.string.card_type),
                value = "$memberTypeStr - ${card.identifier}"
            )

            InfoRow(
                label = stringResource(id = R.string.department_faculty_label),
                value = card.department
            )

            VehicleInfoRow(
                label = stringResource(id = R.string.vehicle),
                vehicleType = card.vehicleType
            )

            InfoRow(
                label = stringResource(id = R.string.registration_date),
                value = if (card.registeredAt != 0L) dateFormatter.format(Date(card.registeredAt)) else "-"
            )
            InfoRow(
                label = stringResource(id = R.string.last_used),
                value = if (card.lastUsedAt != 0L) dateTimeFormatter.format(Date(card.lastUsedAt)) else "-"
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.SPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = LightOnSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun VehicleInfoRow(
    label: String,
    vehicleType: VehicleType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.SPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            val (iconRes, typeNameRes) = when (vehicleType) {
                VehicleType.CAR -> Pair(R.drawable.ic_car, R.string.car)
                VehicleType.MOTORBIKE -> Pair(R.drawable.ic_motorcycle, R.string.motorcycle)
            }

            Text(
                text = stringResource(id = typeNameRes),
                color = LightOnSurface,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = LightOnSurface
            )
        }
    }
}
