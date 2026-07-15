package com.example.smarttrafficradar.features.management.presentation.ui.register_card

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.presentation.viewmodel.ManagementRegisterCardViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16

@Composable
fun RegisterCardScreen(
    onBackClick: () -> Unit,
    uid: String,
    vehicleType: VehicleType,
    timestamp: Long,
    profileViewModel: UserProfileViewModel = hiltViewModel(),
    managementViewModel: ManagementRegisterCardViewModel = hiltViewModel()
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val managementState by managementViewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uid) {
        profileViewModel.loadUserProfile(uid)
    }

    LaunchedEffect(managementState.isSuccess) {
        if (managementState.isSuccess) {
            Toast.makeText(context, context.getString(R.string.registration_complete), Toast.LENGTH_SHORT).show()
            onBackClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = profileState) {
            is UserProfileState.Loading, UserProfileState.Idle -> {
                CircularProgressIndicator(color = LightPrimary)
            }

            is UserProfileState.Success -> {
                val profile = state.profile

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .verticalScroll(rememberScrollState())
                ) {
                    RegisterCardTopBar(onBackClick = onBackClick)

                    Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                    RequesterInfoSection(profile = profile)

                    Spacer(modifier = Modifier.height(AppSpacing.MPlus))

                    RegistrationRequestSection(vehicleType = vehicleType, timestamp = timestamp)

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    if (managementState.isScanning) {
                        ScanInstructionSection(timeLeft = managementState.scanTimeLeft)
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimen.PaddingM)
                        ) {
                            Button(
                                onClick = { managementViewModel.startScanning() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimen.HeightLarge),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
                            ) {
                                Icon(imageVector = Icons.Default.Nfc, contentDescription = null)
                                Spacer(modifier = Modifier.padding(horizontal = AppSpacing.XS))
                                Text(
                                    text = stringResource(R.string.scan_card),
                                    style = MaterialTheme.typography.s16,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    managementState.scannedUid?.let { scannedUid ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimen.PaddingM),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.scanned_uid, scannedUid),
                                style = MaterialTheme.typography.s16,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )

                            Spacer(modifier = Modifier.height(AppSpacing.M))

                            Button(
                                onClick = { managementViewModel.approveRegistration(uid, scannedUid) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimen.HeightLarge),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                enabled = !managementState.isLoading
                            ) {
                                if (managementState.isLoading) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                } else {
                                    Text(
                                        text = stringResource(R.string.confirm_registration),
                                        style = MaterialTheme.typography.s16,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(AppSpacing.L))
                    }

                    FeeInformationSection()

                    Spacer(modifier = Modifier.height(AppSpacing.XXL))
                }
            }

            is UserProfileState.Error -> {
                Text(
                    text = state.uiText.asString(),
                    style = MaterialTheme.typography.s15,
                    color = ActionDanger
                )
            }
        }
    }
}
