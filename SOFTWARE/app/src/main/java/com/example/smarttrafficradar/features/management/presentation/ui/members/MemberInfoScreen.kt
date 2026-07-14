package com.example.smarttrafficradar.features.management.presentation.ui.members

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.presentation.ui.registerd_card.CardDetailScreen
import com.example.smarttrafficradar.features.management.presentation.ui.registerd_card.CardDetailTopBar
import com.example.smarttrafficradar.features.management.presentation.viewmodel.MemberInfoState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.MemberInfoViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.utils.medium
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16

@Composable
fun MemberInfoScreen(
    member: OrganizationMember,
    onBackClick: () -> Unit,
    viewModel: MemberInfoViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (member.linkedUid != null) {
            val state by viewModel.state.collectAsState()

            LaunchedEffect(member.linkedUid) {
                viewModel.getRegisteredCard(member.linkedUid)
            }

            when (val currentState = state) {
                is MemberInfoState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = SmartBlue)
                    }
                }

                is MemberInfoState.Success -> {
                    CardDetailScreen(
                        onBackClick = onBackClick,
                        registeredCard = currentState.card,
                        onBlock = { viewModel.updateCardStatus(it, CardStatus.BLOCKED) },
                        onActive = { viewModel.updateCardStatus(it, CardStatus.ACTIVE) }
                    )
                }

                is MemberInfoState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = currentState.message,
                            color = ActionDanger,
                            style = MaterialTheme.typography.s16.medium()
                        )
                    }
                }

                else -> {}
            }
        } else {
            UnregisteredMemberInfo(member = member, onBackClick = onBackClick)
        }
    }
}

@Composable
private fun UnregisteredMemberInfo(
    member: OrganizationMember,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CardDetailTopBar(
            rfidUid = stringResource(id = R.string.unregistered),
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(AppShape.ShapeL),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = stringResource(id = R.string.personal_info),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightOnSurface,
                    modifier = Modifier.padding(bottom = AppSpacing.MPlus)
                )

                MemberInfoRow(
                    label = stringResource(id = R.string.full_name_label),
                    value = member.fullName
                )

                val idLabel = if (member.memberType == "STUDENT") {
                    stringResource(id = R.string.student_id_label)
                } else {
                    stringResource(id = R.string.employee_id_label)
                }
                MemberInfoRow(label = idLabel, value = member.identifier)

                MemberInfoRow(
                    label = stringResource(id = R.string.email_address_label),
                    value = member.email
                )

                MemberInfoRow(
                    label = stringResource(id = R.string.department_faculty_label),
                    value = member.department
                )

                val typeValue = if (member.memberType == "STUDENT") {
                    stringResource(id = R.string.student)
                } else {
                    stringResource(id = R.string.employee)
                }
                MemberInfoRow(
                    label = stringResource(id = R.string.card_type),
                    value = typeValue
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.card_status),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .height(25.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeM))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.unregistered),
                            style = MaterialTheme.typography.s15,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AppSpacing.SPlus),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = LightOnSurface,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}