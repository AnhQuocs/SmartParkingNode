package com.example.smarttrafficradar.features.management.presentation.ui

import android.content.Intent
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.management.presentation.ui.registerd_card.RegisteredCardsActivity
import com.example.smarttrafficradar.features.management.presentation.ui.registration_requests.RegistrationRequestsActivity
import com.example.smarttrafficradar.features.management.presentation.viewmodel.OrganizationMemberListState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.OrganizationMemberListViewModel
import com.example.smarttrafficradar.features.management.presentation.viewmodel.PendingCardsState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.PendingCardsViewModel
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14

@Composable
fun ManagementScreen(
    registrationListViewModel: RegistrationListViewModel = hiltViewModel(),
    organizationMemberListViewModel: OrganizationMemberListViewModel = hiltViewModel(),
    pendingCardsViewModel: PendingCardsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val registrationState by registrationListViewModel.state.collectAsState()
    val organizationMemberState by organizationMemberListViewModel.state.collectAsState()
    val pendingCardsState by pendingCardsViewModel.state.collectAsState()

    val scrollState = rememberScrollState()

    val isLoading = registrationState is RegistrationListState.Loading ||
            organizationMemberState is OrganizationMemberListState.Loading ||
            pendingCardsState is PendingCardsState.Loading

    val errorMessage = (registrationState as? RegistrationListState.Error)?.message
        ?: (organizationMemberState as? OrganizationMemberListState.Error)?.message
        ?: (pendingCardsState as? PendingCardsState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ManagementTopBar()

            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimen.PaddingXXL),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SmartBlue)
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingL),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.s14,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                val registrationRequests =
                    (registrationState as? RegistrationListState.Success)?.requests ?: emptyList()
                val registeredCards =
                    (registrationState as? RegistrationListState.Success)?.cards ?: emptyList()
                val users =
                    (organizationMemberState as? OrganizationMemberListState.Success)?.members
                        ?: emptyList()

                ManagementCategoriesSection(
                    onRegistrationRequests = {
                        val intent = Intent(context, RegistrationRequestsActivity::class.java)
                        context.startActivity(intent)
                    },
                    onRegisteredCardsClick = {
                        val intent = Intent(context, RegisteredCardsActivity::class.java)
                        context.startActivity(intent)
                    },
                    onUserListClick = {
                        // Navigate to user list
                    },
                    registrationRequests = registrationRequests,
                    registeredCards = registeredCards,
                    users = users
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                // Hiển thị phần thẻ vi phạm/nợ phí đang chờ xử lý
                UnpaidCardSection(
                    viewModel = pendingCardsViewModel,
                    state = pendingCardsState
                )
            }
        }
    }
}
