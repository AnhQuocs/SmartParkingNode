package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.viewmodel.RegisterCardState
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AmberOrange
import com.example.smarttrafficradar.ui.theme.AmberOrangeLight
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.ErrorRed
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun LockCardSection(
    onConfirm: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(Dimen.SizeS)
        )
        Spacer(modifier = Modifier.width(AppSpacing.S))
        Text(
            text = stringResource(id = R.string.lock_card),
            style = MaterialTheme.typography.s16.semiBold()
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.confirm_block_title),
                    style = MaterialTheme.typography.s16.semiBold()
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.confirm_block_message),
                    style = MaterialTheme.typography.s14
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onConfirm()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.lock_card),
                        color = ErrorRed,
                        style = MaterialTheme.typography.s14.semiBold()
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = SlateGray,
                        style = MaterialTheme.typography.s14
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(AppShape.ShapeL)
        )
    }
}

@Composable
fun RegisterCardForm(
    state: RegisterCardState,
    onVehicleTypeSelected: (VehicleType) -> Unit,
    onSendRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimen.PaddingL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(AmberOrangeLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = null,
                        tint = AmberOrange,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    text = stringResource(id = R.string.need_to_register_rfid),
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = LightOnSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.register_instruction_desc),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.XL))

                GuideSection()

                Spacer(modifier = Modifier.height(AppSpacing.XL))

                OfficeInfoSection()
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.L))

        VehicleSelectionSection(
            selectedType = state.selectedVehicleType,
            onTypeSelected = onVehicleTypeSelected
        )

        Spacer(modifier = Modifier.height(AppSpacing.L))

        Button(
            onClick = onSendRequest,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightLarge),
            shape = RoundedCornerShape(AppShape.ShapeXXL),
            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
        ) {
            Text(
                text = stringResource(id = R.string.send_registration_request),
                style = MaterialTheme.typography.s16.semiBold()
            )
        }
    }
}

@Composable
fun GuideSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.register_guide),
            style = MaterialTheme.typography.s16.semiBold(),
            color = LightOnSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))

        GuideItem(number = "1", text = stringResource(id = R.string.register_step_1))
        GuideItem(number = "2", text = stringResource(id = R.string.register_step_2))
        GuideItem(number = "3", text = stringResource(id = R.string.register_step_3))
    }
}

@Composable
fun GuideItem(number: String, text: String) {
    Row(
        modifier = Modifier.padding(vertical = AppSpacing.S),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(SmartBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                style = MaterialTheme.typography.s12.semiBold()
            )
        }
        Spacer(modifier = Modifier.width(AppSpacing.M))
        Text(
            text = text,
            style = MaterialTheme.typography.s14,
            color = SlateGray
        )
    }
}

@Composable
fun OfficeInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .background(Background)
            .padding(Dimen.PaddingM)
    ) {
        Text(
            text = stringResource(id = R.string.office_address_title),
            style = MaterialTheme.typography.s14.semiBold(),
            color = SmartBlue
        )
        Spacer(modifier = Modifier.height(AppSpacing.S))
        Text(
            text = stringResource(id = R.string.office_location),
            style = MaterialTheme.typography.s12,
            color = SlateGray
        )
        Text(
            text = stringResource(id = R.string.office_hours),
            style = MaterialTheme.typography.s12,
            color = SlateGray
        )
        Text(
            text = stringResource(id = R.string.office_phone),
            style = MaterialTheme.typography.s12,
            color = SlateGray
        )
    }
}

@Composable
fun VehicleSelectionSection(
    selectedType: VehicleType,
    onTypeSelected: (VehicleType) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.select_vehicle_type),
            style = MaterialTheme.typography.s16.semiBold(),
            color = LightOnSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(modifier = Modifier.fillMaxWidth()) {
            VehicleTypeItem(
                modifier = Modifier.weight(1f),
                type = VehicleType.MOTORBIKE,
                label = stringResource(id = R.string.motorcycle),
                icon = R.drawable.ic_motorcycle,
                isSelected = selectedType == VehicleType.MOTORBIKE,
                onClick = { onTypeSelected(VehicleType.MOTORBIKE) }
            )
            Spacer(modifier = Modifier.width(AppSpacing.M))
            VehicleTypeItem(
                modifier = Modifier.weight(1f),
                type = VehicleType.CAR,
                label = stringResource(id = R.string.car),
                icon = R.drawable.ic_car,
                isSelected = selectedType == VehicleType.CAR,
                onClick = { onTypeSelected(VehicleType.CAR) }
            )
        }
    }
}

@Composable
fun VehicleTypeItem(
    modifier: Modifier = Modifier,
    type: VehicleType,
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .background(if (isSelected) SmartBlue.copy(alpha = 0.1f) else Color.White),
        shape = RoundedCornerShape(AppShape.ShapeM),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(
            2.dp,
            SmartBlue
        ) else null,
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color.White else Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeL),
                tint = if (isSelected) SmartBlue else SlateGray
            )
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Text(
                text = label,
                style = MaterialTheme.typography.s14.semiBold(),
                color = if (isSelected) SmartBlue else SlateGray
            )
        }
    }
}