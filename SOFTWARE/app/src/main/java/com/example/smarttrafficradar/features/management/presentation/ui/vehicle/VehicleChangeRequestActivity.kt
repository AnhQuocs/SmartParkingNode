package com.example.smarttrafficradar.features.management.presentation.ui.vehicle

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.BaseComponentActivity
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
import com.example.smarttrafficradar.features.management.presentation.viewmodel.VehicleChangeRequestsViewModel
import com.example.smarttrafficradar.ui.theme.LightPrimary
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleChangeRequestActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var selectedVehicleChangeRequest by remember {
                mutableStateOf<VehicleChangeRequest?>(null)
            }

            val vehicleChangeRequestsViewModel: VehicleChangeRequestsViewModel = hiltViewModel()
            val isProcessing by vehicleChangeRequestsViewModel.isProcessing.collectAsState()

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                VehicleChangeRequestScreen(
                    onBackClick = { finish() },
                    onClick = { request ->
                        selectedVehicleChangeRequest = request
                    }
                )

                selectedVehicleChangeRequest?.let { request ->
                    ChangeRequestDetailScreen(
                        vehicleChangeRequest = request,
                        onBackClick = {
                            selectedVehicleChangeRequest = null
                        },
                        onReject = {
                            vehicleChangeRequestsViewModel.rejectRequest(request.uid)
                            selectedVehicleChangeRequest = null
                        },
                        onApprove = {
                            vehicleChangeRequestsViewModel.approveRequest(request.uid)
                            selectedVehicleChangeRequest = null
                        }
                    )
                }

                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        awaitPointerEvent()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LightPrimary)
                    }
                }
            }
        }
    }
}
