package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightOnSurface
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

    LaunchedEffect(state.isSuccess, state.isVehicleChangeSuccess, state.isLockCardSuccess) {
        if (state.isSuccess || state.isVehicleChangeSuccess || state.isLockCardSuccess) {
            val message = when {
                state.isSuccess -> context.getString(R.string.registration_request_sent)
                state.isVehicleChangeSuccess -> context.getString(R.string.request_sent_success)
                state.isLockCardSuccess -> context.getString(R.string.card_locked_success)
                else -> ""
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.resetSuccess()
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
                    IconButton(
                        onClick = if (state.showChangeLockView) {
                            { viewModel.setShowChangeLockView(false) }
                        } else onBack) {
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
            if (state.isLoading && !state.isAlreadyRegistered && !state.isLocked) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SmartBlue
                )
            } else if (state.showChangeLockView) {
                ChangeLockCardView(
                    state = state,
                    onVehicleChange = { viewModel.sendVehicleChangeRequest(uid) },
                    onLockCard = { viewModel.lockCard(uid) }
                )
            } else if (state.isLocked) {
                LockedCardView(onBack = onBack)
            } else if (state.isAlreadyRegistered) {
                AlreadyRegisteredView(
                    rfidUid = state.currentRfidUid ?: "",
                    vehicleType = state.currentVehicleType,
                    onBack = onBack,
                    onRequestChange = { viewModel.setShowChangeLockView(true) }
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
    onBack: () -> Unit,
    onRequestChange: () -> Unit
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
            onClick = onRequestChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightLarge),
            shape = RoundedCornerShape(AppShape.ShapeXXL),
            colors = ButtonDefaults.buttonColors(containerColor = AmberOrange)
        ) {
            Text(
                text = stringResource(id = R.string.request_change_lock_card),
                style = MaterialTheme.typography.s16.semiBold()
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

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
fun ChangeLockCardView(
    state: RegisterCardState,
    onVehicleChange: () -> Unit,
    onLockCard: () -> Unit
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
                        .size(Dimen.SizeMega)
                        .background(SmartBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SmartBlue,
                        modifier = Modifier.size(Dimen.SizeXLPlus)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    text = stringResource(id = R.string.change_vehicle_request_title),
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

                // Option 1: Change Vehicle Type
                VehicleChangeSection(
                    currentType = state.currentVehicleType ?: VehicleType.MOTORBIKE,
                    onConfirm = onVehicleChange
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                // Option 2: Lock Card
                LockCardSection(
                    onConfirm = onLockCard
                )
            }
        }
    }
}

@Composable
fun VehicleChangeSection(
    currentType: VehicleType,
    onConfirm: () -> Unit
) {
    val nextType = if (currentType == VehicleType.CAR) VehicleType.MOTORBIKE else VehicleType.CAR
    val currentLabel =
        if (currentType == VehicleType.CAR) stringResource(R.string.car) else stringResource(R.string.motorcycle)
    val nextLabel =
        if (nextType == VehicleType.CAR) stringResource(R.string.car) else stringResource(R.string.motorcycle)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background, RoundedCornerShape(AppShape.ShapeM))
            .padding(Dimen.PaddingM)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
                tint = SmartBlue
            )
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Text(
                text = stringResource(id = R.string.change_vehicle_type),
                style = MaterialTheme.typography.s16.semiBold(),
                color = SmartBlue
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.current_vehicle),
                    style = MaterialTheme.typography.s12,
                    color = SlateGray
                )
                Text(text = currentLabel, style = MaterialTheme.typography.s14.semiBold())
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeS),
                tint = SlateGray
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.new_vehicle),
                    style = MaterialTheme.typography.s12,
                    color = SlateGray
                )
                Text(text = nextLabel, style = MaterialTheme.typography.s14.semiBold())
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeM),
            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
        ) {
            Text(text = stringResource(id = R.string.confirm_change))
        }
    }
}
