package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.dimens.Dimen.PaddingS
import com.example.smarttrafficradar.ui.theme.NatureBackground
import com.example.smarttrafficradar.ui.theme.NatureGreen
import com.example.smarttrafficradar.ui.theme.NeonGreen
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

data class CardStatus(
    val bgrColor: Color,
    val contentColor: Color,
    val icon: ImageVector,
    val activeStatus: String,
    val rfidCardCode: String?,
    val vehicle: String?,
)

@Composable
fun ParkingCard(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    rfidUid: String? = null,
    vehicleType: VehicleType? = null,
) {
    val vehicle = if (vehicleType == VehicleType.CAR)
        stringResource(id = R.string.car)
    else
        stringResource(id = R.string.motorcycle)

    val vehicleIcon = if (vehicleType == VehicleType.CAR)
        Icons.Default.DirectionsCar
    else
        Icons.Default.Motorcycle

    val cardStatus = if (isActive) {
        CardStatus(
            bgrColor = NatureBackground,
            contentColor = NeonGreen,
            icon = Icons.Default.CheckCircleOutline,
            activeStatus = stringResource(id = R.string.activated),
            rfidCardCode = rfidUid,
            vehicle = vehicle,
        )
    } else {
        CardStatus(
            bgrColor = SlateMist,
            contentColor = Color.Gray,
            icon = Icons.Default.HourglassEmpty,
            activeStatus = stringResource(id = R.string.not_activated),
            rfidCardCode = stringResource(id = R.string.not_activated),
            vehicle = stringResource(id = R.string.not_activated)
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingML)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.parking_card),
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .clip(RoundedCornerShape(AppShape.ShapeXS))
                        .background(color = cardStatus.bgrColor)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = Dimen.PaddingS)
                    ) {
                        Icon(
                            imageVector = cardStatus.icon,
                            contentDescription = null,
                            tint = cardStatus.contentColor,
                            modifier = Modifier.size(Dimen.SizeSM)
                        )

                        Spacer(modifier = Modifier.width(AppSpacing.S))

                        Text(
                            text = cardStatus.activeStatus,
                            color = cardStatus.contentColor,
                            style = MaterialTheme.typography.s14
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.XLPlus))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.rfid_card_code),
                    style = MaterialTheme.typography.s16,
                    color = Color.Black
                )

                Text(
                    text = cardStatus.rfidCardCode ?: "",
                    style = MaterialTheme.typography.s16.bold(),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.vehicle_type),
                    style = MaterialTheme.typography.s16,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vehicleIcon,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(Dimen.SizeSM)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.XXS))

                    Text(
                        text = cardStatus.vehicle ?: "",
                        style = MaterialTheme.typography.s16.bold(),
                        color = Color.Black
                    )
                }
            }
        }
    }
}