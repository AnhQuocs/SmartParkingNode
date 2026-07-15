package com.example.smarttrafficradar.features.management.presentation.ui.register_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegistrationRequestSection(
    vehicleType: VehicleType,
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    val dateTimeFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimen.PaddingXXS),
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
                text = stringResource(id = R.string.registration_request_details),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LightOnSurface,
                modifier = Modifier.padding(bottom = AppSpacing.MPlus)
            )

            val vehicleTypeStr = when (vehicleType) {
                VehicleType.CAR -> stringResource(id = R.string.car)
                VehicleType.MOTORBIKE -> stringResource(id = R.string.motorcycle)
            }
            InfoRow(
                label = stringResource(id = R.string.vehicle_type),
                value = vehicleTypeStr
            )

            InfoRow(
                label = stringResource(id = R.string.request_time),
                value = if (timestamp != 0L) dateTimeFormatter.format(Date(timestamp)) else "-"
            )
        }
    }
}
