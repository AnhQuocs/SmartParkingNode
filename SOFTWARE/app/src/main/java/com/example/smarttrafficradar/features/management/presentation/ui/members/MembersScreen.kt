package com.example.smarttrafficradar.features.management.presentation.ui.members

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.presentation.viewmodel.OrganizationMemberListState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.OrganizationMemberListViewModel
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileState
import com.example.smarttrafficradar.features.user_profile.presentation.viewmodel.UserProfileViewModel
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.medium
import com.example.smarttrafficradar.utils.s16

@Composable
fun MembersScreen(
    onBackClick: () -> Unit,
    organizationMemberListViewModel: OrganizationMemberListViewModel = hiltViewModel()
) {
    val state by organizationMemberListViewModel.state.collectAsState()
    var selectedMember by remember { mutableStateOf<OrganizationMember?>(null) }

    if (selectedMember != null) {
        MemberInfoScreen(
            member = selectedMember!!,
            onBackClick = { selectedMember = null }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Background)
        ) {
            MembersTopBar(onBackClick = onBackClick)

            when (val currentState = state) {
                is OrganizationMemberListState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SmartBlue)
                    }
                }

                is OrganizationMemberListState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.message,
                            style = MaterialTheme.typography.s16.medium(),
                            color = ActionDanger,
                            modifier = Modifier.padding(Dimen.PaddingM)
                        )
                    }
                }

                is OrganizationMemberListState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = Dimen.PaddingM)
                    ) {
                        items(currentState.members, key = { it.identifier }) { member ->
                            // Tạo/Lấy instance ViewModel riêng cho từng member dựa trên linkedUid
                            val profile = if (member.linkedUid != null) {
                                val itemViewModel: UserProfileViewModel = hiltViewModel(key = member.linkedUid)
                                val profileState by itemViewModel.profileState.collectAsState()

                                LaunchedEffect(member.linkedUid) {
                                    itemViewModel.loadUserProfile(member.linkedUid)
                                }

                                (profileState as? UserProfileState.Success)?.profile
                            } else null

                            MemberItem(
                                member = member,
                                profile = profile,
                                onClick = {
                                    selectedMember = member
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
