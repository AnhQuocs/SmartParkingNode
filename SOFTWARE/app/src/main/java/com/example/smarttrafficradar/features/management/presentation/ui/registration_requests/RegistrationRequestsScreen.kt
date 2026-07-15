package com.example.smarttrafficradar.features.management.presentation.ui.registration_requests

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegistrationRequestsScreen(
    onBack: () -> Unit,
    navController: NavController,
    registrationListViewModel: RegistrationListViewModel = hiltViewModel()
) {
    val registrationListState by registrationListViewModel.state.collectAsState()
    var requestToReject by remember { mutableStateOf<RegistrationRequest?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = registrationListState) {
            is RegistrationListState.Loading -> {
                CircularProgressIndicator(color = LightPrimary)
            }

            is RegistrationListState.Success -> {
                val requests = state.requests

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    RegistrationRequestsTopBar(
                        onBackClick = onBack,
                        count = requests.size
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(requests, key = { it.id }) { request ->
                            RegistrationRequestItem(
                                request = request,
                                onRegister = {
                                    navController.navigate(
                                        "register_card/${request.uid}/${request.vehicleType.name}/${request.timestamp}"
                                    )
                                },
                                onReject = {
                                    requestToReject = request
                                }
                            )

                            Spacer(modifier = Modifier.height(AppSpacing.M))
                        }
                    }
                }
            }

            is RegistrationListState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.s16,
                    color = ActionDanger
                )
            }
        }
    }

    // Confirmation Dialog for Rejecting Request
    requestToReject?.let { request ->
        AlertDialog(
            onDismissRequest = { requestToReject = null },
            title = {
                Text(
                    text = stringResource(id = R.string.reject_request_title),
                    style = MaterialTheme.typography.s16.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.reject_request_message),
                    style = MaterialTheme.typography.s14
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        registrationListViewModel.rejectRequest(request.id)
                        requestToReject = null
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.reject),
                        color = ActionDanger
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { requestToReject = null }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(AppShape.ShapeL)
        )
    }
}

@Composable
fun RegistrationRequestItem(
    request: RegistrationRequest,
    onRegister: () -> Unit,
    onReject: () -> Unit
) {
    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(request.timestamp))

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM),
        shape = RoundedCornerShape(AppShape.ShapeL)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(
                        color = Color(0xFFFFB300),
                        shape = RoundedCornerShape(
                            topStart = AppShape.ShapeL,
                            bottomStart = AppShape.ShapeL
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = "${request.fullName} - ${request.identifier}",
                    style = MaterialTheme.typography.s16.copy(fontWeight = FontWeight.Medium),
                    color = RoyalBlue
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(Dimen.SizeS),
                        tint = SlateGray
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.XS))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.s14,
                        color = SlateGray
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.M))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeL))
                            .background(GreenBright.copy(alpha = 0.1f))
                            .clickable { onRegister() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = Dimen.PaddingS)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = GreenBright,
                                modifier = Modifier.size(Dimen.SizeSM)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.S))
                            Text(
                                text = stringResource(id = R.string.register),
                                style = MaterialTheme.typography.s15,
                                color = GreenBright
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(AppShape.ShapeL))
                            .background(ActionDanger.copy(alpha = 0.1f))
                            .clickable { onReject() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = Dimen.PaddingS)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = null,
                                tint = ActionDanger,
                                modifier = Modifier.size(Dimen.SizeSM)
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.S))
                            Text(
                                text = stringResource(id = R.string.reject),
                                style = MaterialTheme.typography.s15,
                                color = ActionDanger
                            )
                        }
                    }
                }
            }
        }
    }
}
