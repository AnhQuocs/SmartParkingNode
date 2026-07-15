package com.example.smarttrafficradar.features.management.presentation.ui.register_card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.TextSecondary

@Composable
fun RequesterInfoSection(
    profile: UserProfile,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimen.PaddingXXS),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(AppShape.ShapeL),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(id = R.string.requester_information),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = LightOnSurface,
                modifier = Modifier.padding(bottom = AppSpacing.MPlus)
            )

            InfoRow(label = stringResource(id = R.string.full_name_label), value = profile.fullName)

            val memberTypeStr = when (profile.memberType) {
                MemberType.STUDENT -> stringResource(id = R.string.student)
                MemberType.EMPLOYEE -> stringResource(id = R.string.employee)
            }
            InfoRow(
                label = stringResource(id = R.string.card_type),
                value = "$memberTypeStr - ${profile.identifier}"
            )

            InfoRow(
                label = stringResource(id = R.string.department_faculty_label),
                value = profile.department
            )

            InfoRow(
                label = stringResource(id = R.string.phone_number_label),
                value = profile.phoneNumber
            )

            InfoRow(
                label = stringResource(id = R.string.email_address_label),
                value = profile.email
            )
        }
    }
}

@Composable
fun InfoRow(
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
