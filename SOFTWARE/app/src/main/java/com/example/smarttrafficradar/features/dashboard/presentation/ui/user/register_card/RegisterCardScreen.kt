package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.dashboard.presentation.viewmodel.RegisterCardState
import com.example.smarttrafficradar.features.dashboard.presentation.viewmodel.RegisterCardViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AmberOrange
import com.example.smarttrafficradar.ui.theme.AmberOrangeLight
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightOnPrimaryContainer
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCardScreen(
    uid: String,
    onBack: () -> Unit,
    viewModel: RegisterCardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uid) {
        viewModel.observeRegistrationStatus(uid)
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(
                context,
                context.getString(R.string.registration_request_sent),
                Toast.LENGTH_SHORT
            ).show()
            onBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.register_card_title),
                        style = MaterialTheme.typography.s18.semiBold(),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SmartBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            if (state.isLoading && !state.isAlreadyRegistered) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SmartBlue
                )
            } else if (state.isAlreadyRegistered) {
                AlreadyRegisteredView(
                    rfidUid = state.currentRfidUid ?: "",
                    vehicleType = state.currentVehicleType,
                    onBack = onBack
                )
            } else {
                RegisterCardForm(
                    state = state,
                    onVehicleTypeSelected = viewModel::onVehicleTypeSelected,
                    onSendRequest = { viewModel.sendRegistrationRequest(uid) }
                )
            }
        }
    }
}

@Composable
fun AlreadyRegisteredView(
    rfidUid: String,
    vehicleType: VehicleType?,
    onBack: () -> Unit
) {
    val vehicleLabel = when (vehicleType) {
        VehicleType.MOTORBIKE -> stringResource(id = R.string.motorcycle)
        VehicleType.CAR -> stringResource(id = R.string.car)
        else -> "N/A"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    text = stringResource(id = R.string.card_already_registered_title),
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = LightOnSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.card_already_registered_desc),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Background, RoundedCornerShape(AppShape.ShapeM))
                        .padding(Dimen.PaddingM),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.rfid_code_label, rfidUid),
                        style = MaterialTheme.typography.s16.semiBold(),
                        color = SmartBlue
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.XS))
                    Text(
                        text = stringResource(id = R.string.registered_vehicle_type, vehicleLabel),
                        style = MaterialTheme.typography.s14,
                        color = SlateGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.XL))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightLarge),
            shape = RoundedCornerShape(AppShape.ShapeXXL),
            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
        ) {
            Text(
                text = stringResource(id = R.string.back_to_dashboard),
                style = MaterialTheme.typography.s16.semiBold()
            )
        }
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
            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(id = R.string.send_registration_request),
                    style = MaterialTheme.typography.s16.semiBold()
                )
            }
        }
    }
}

@Composable
fun GuideSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RoyalBlueLight, RoundedCornerShape(AppShape.ShapeM))
            .padding(Dimen.PaddingM)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = RoyalBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Text(
                text = stringResource(id = R.string.register_guide),
                style = MaterialTheme.typography.s16.semiBold(),
                color = LightOnPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        GuideItem(1, stringResource(id = R.string.register_step_1))
        GuideItem(2, stringResource(id = R.string.register_step_2))
        GuideItem(3, stringResource(id = R.string.register_step_3))
    }
}

@Composable
fun GuideItem(step: Int, text: String) {
    Row(
        modifier = Modifier.padding(vertical = Dimen.PaddingXSPlus),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(RoyalBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step.toString(),
                style = MaterialTheme.typography.s12.semiBold(),
                color = RoyalBlue
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
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.office_address_title),
            style = MaterialTheme.typography.s16.semiBold(),
            color = LightOnSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))

        InfoItem(stringResource(id = R.string.office_location))
        InfoItem(stringResource(id = R.string.office_hours))
        InfoItem(stringResource(id = R.string.office_phone))
    }
}

@Composable
fun InfoItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.s14,
        color = SlateGray,
        modifier = Modifier.padding(vertical = 2.dp)
    )
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
                label = stringResource(id = R.string.motorcycle),
                isSelected = selectedType == VehicleType.MOTORBIKE,
                onClick = { onTypeSelected(VehicleType.MOTORBIKE) }
            )
            Spacer(modifier = Modifier.width(AppSpacing.M))
            VehicleTypeItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.car),
                isSelected = selectedType == VehicleType.CAR,
                onClick = { onTypeSelected(VehicleType.CAR) }
            )
        }
    }
}

@Composable
fun VehicleTypeItem(
    modifier: Modifier = Modifier,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .background(if (isSelected) SmartBlue.copy(alpha = 0.1f) else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) SmartBlue else Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(AppShape.ShapeM)
            )
            .clickable { onClick() }
            .padding(Dimen.PaddingM),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.s14.semiBold(),
            color = if (isSelected) SmartBlue else SlateGray
        )
    }
}
