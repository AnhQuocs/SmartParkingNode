package com.example.smarttrafficradar.features.management.presentation.ui.vehicle

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.presentation.ui.registerd_card.CardDetailTopBar
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.ErrorRed
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.medium
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun ChangeRequestDetailScreen(
    onApprove: () -> Unit,
    onReject: () -> Unit,
    vehicleChangeRequest: VehicleChangeRequest,
    onBackClick: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    val memberTypeStr = when (vehicleChangeRequest.memberType) {
        MemberType.STUDENT -> stringResource(id = R.string.student)
        MemberType.EMPLOYEE -> stringResource(id = R.string.employee)
    }

    BackHandler { onBackClick() }

    Scaffold(
        topBar = {
            CardDetailTopBar(
                rfidUid = vehicleChangeRequest.rfidUid,
                text = stringResource(id = R.string.request_details),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            ActionButtonsRow(
                onRejectClick = { showRejectDialog = true },
                onApproveClick = { showApproveDialog = true }
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            DetailSectionHeader(title = stringResource(id = R.string.requester_information))
            DetailCard {
                InfoRow(
                    label = stringResource(id = R.string.full_name_label),
                    value = vehicleChangeRequest.fullName
                )
                InfoRow(
                    label = stringResource(id = R.string.card_type),
                    value = "$memberTypeStr - ${vehicleChangeRequest.identifier}"
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            // Vehicle Change Details
            DetailSectionHeader(title = stringResource(id = R.string.vehicle_change_requests))
            DetailCard {
                VehicleChangeComparison(
                    currentType = vehicleChangeRequest.currentVehicleType,
                    requestedType = vehicleChangeRequest.requestedVehicleType
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))
        }
    }

    // Confirmation Dialogs
    if (showApproveDialog) {
        ConfirmActionDialog(
            title = stringResource(id = R.string.approve_change_title),
            message = stringResource(id = R.string.approve_change_message),
            onConfirm = {
                showApproveDialog = false
                onApprove()
            },
            onDismiss = { showApproveDialog = false },
            confirmColor = LightPrimary
        )
    }

    if (showRejectDialog) {
        ConfirmActionDialog(
            title = stringResource(id = R.string.reject_change_title),
            message = stringResource(id = R.string.reject_change_message),
            onConfirm = {
                showRejectDialog = false
                onReject()
            },
            onDismiss = { showRejectDialog = false },
            confirmColor = ErrorRed
        )
    }
}

@Composable
fun DetailSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.s12.semiBold(),
        color = SlateGray,
        modifier = Modifier.padding(bottom = AppSpacing.S, start = AppSpacing.XS)
    )
}

@Composable
fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(AppShape.ShapeM),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(Dimen.PaddingM),
            content = content
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = AppSpacing.XS)) {
        Text(text = label, style = MaterialTheme.typography.s12, color = SlateGray)
        Text(text = value, style = MaterialTheme.typography.s15.medium(), color = LightOnSurface)
    }
}

@Composable
fun VehicleChangeComparison(currentType: VehicleType, requestedType: VehicleType) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        VehicleTypeInfo(
            label = stringResource(id = R.string.current_vehicle),
            type = currentType,
            color = SlateGray
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = SlateGray,
            modifier = Modifier.size(Dimen.SizeM)
        )

        VehicleTypeInfo(
            label = stringResource(id = R.string.new_vehicle),
            type = requestedType,
            color = SmartBlue
        )
    }
}

@Composable
fun VehicleTypeInfo(label: String, type: VehicleType, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.s12, color = SlateGray)
        Text(
            text = stringResource(id = if (type == VehicleType.CAR) R.string.car else R.string.motorcycle),
            style = MaterialTheme.typography.s16.bold(),
            color = color
        )
    }
}

@Composable
fun ActionButtonsRow(onRejectClick: () -> Unit, onApproveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(Dimen.PaddingM),
        horizontalArrangement = Arrangement.spacedBy(Dimen.PaddingM)
    ) {
        OutlinedButton(
            onClick = onRejectClick,
            modifier = Modifier
                .weight(1f)
                .height(Dimen.HeightDefault),
            shape = RoundedCornerShape(AppShape.ShapeM),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    ErrorRed
                )
            )
        ) {
            Text(
                text = stringResource(id = R.string.reject),
                color = ErrorRed,
                style = MaterialTheme.typography.s16.semiBold()
            )
        }

        Button(
            onClick = onApproveClick,
            modifier = Modifier
                .weight(1f)
                .height(Dimen.HeightDefault),
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
        ) {
            Text(
                text = stringResource(id = R.string.approve),
                color = Color.White,
                style = MaterialTheme.typography.s16.semiBold()
            )
        }
    }
}

@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.s18.bold()) },
        text = { Text(text = message, style = MaterialTheme.typography.s14) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(id = R.string.confirm_change),
                    color = confirmColor,
                    style = MaterialTheme.typography.s14.bold()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = SlateGray,
                    style = MaterialTheme.typography.s14.medium()
                )
            }
        },
        shape = RoundedCornerShape(AppShape.ShapeL),
        containerColor = Color.White
    )
}