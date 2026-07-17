package com.example.smarttrafficradar.features.management.presentation.ui.vehicle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.presentation.viewmodel.VehicleChangeRequestsState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.VehicleChangeRequestsViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.*
import com.example.smarttrafficradar.utils.*

@Composable
fun VehicleChangeRequestScreen(
    onBackClick: () -> Unit,
    onClick: (VehicleChangeRequest) -> Unit,
    vehicleChangeRequestsViewModel: VehicleChangeRequestsViewModel = hiltViewModel()
) {

    val vehicleState by vehicleChangeRequestsViewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
        contentAlignment = Alignment.Center
    ) {
        when(val state = vehicleState) {
            is VehicleChangeRequestsState.Loading -> {
                CircularProgressIndicator(color = LightPrimary)
            }

            is VehicleChangeRequestsState.Success -> {
                val requests = state.requests

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    VehicleChangeRequestTopBar(
                        onBackClick = onBackClick,
                        count = requests.size
                    )

                    if(requests.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_vehicle_change_requests),
                                style = MaterialTheme.typography.s16,
                                color = SlateGray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = Dimen.PaddingM)
                        ) {
                            items(requests) { request ->
                                VehicleChangeRequestItem(
                                    vehicleChangeRequest = request,
                                    onClick = { onClick(request) }
                                )
                            }
                        }
                    }
                }
            }

            is VehicleChangeRequestsState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.s16,
                    color = ErrorRed
                )
            }
        }
    }
}

@Composable
fun VehicleChangeRequestItem(
    vehicleChangeRequest: VehicleChangeRequest,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingXS)
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(AppShape.ShapeM),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Thông tin cơ bản
                Text(
                    text = vehicleChangeRequest.fullName,
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = LightOnSurface
                )
                
                Spacer(modifier = Modifier.height(AppSpacing.XXS))
                
                Text(
                    text = vehicleChangeRequest.identifier,
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )
                
                Spacer(modifier = Modifier.height(AppSpacing.S))
                
                // Loại xe hiện tại và yêu cầu đổi
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hiện tại
                    Column {
                        Text(
                            text = stringResource(id = R.string.current_vehicle),
                            style = MaterialTheme.typography.s10.medium(),
                            color = SlateGray
                        )
                        Text(
                            text = stringResource(
                                id = if (vehicleChangeRequest.currentVehicleType == VehicleType.CAR) R.string.car 
                                     else R.string.motorcycle
                            ),
                            style = MaterialTheme.typography.s14.semiBold(),
                            color = LightOnSurface
                        )
                    }
                    
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = AppSpacing.M)
                            .size(Dimen.SizeS),
                        tint = SlateGray
                    )
                    
                    // Yêu cầu mới
                    Column {
                        Text(
                            text = stringResource(id = R.string.new_vehicle),
                            style = MaterialTheme.typography.s10.medium(),
                            color = SlateGray
                        )
                        Text(
                            text = stringResource(
                                id = if (vehicleChangeRequest.requestedVehicleType == VehicleType.CAR) R.string.car 
                                     else R.string.motorcycle
                            ),
                            style = MaterialTheme.typography.s14.semiBold(),
                            color = SmartBlue
                        )
                    }
                }
            }
            
            // Icon mũi tên
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM),
                tint = SlateGray
            )
        }
    }
}