package com.example.smarttrafficradar.features.management.presentation.ui.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.ui.theme.SuccessBackground
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.utils.medium
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun MemberItem(
    member: OrganizationMember,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val memberTypeText = when (member.memberType) {
        "STUDENT" -> stringResource(id = R.string.student)
        "EMPLOYEE" -> stringResource(id = R.string.employee)
        else -> ""
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingXSPlus)
            .clickable { onClick() },
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(Dimen.PaddingM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Avatar
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXXL)
                        .clip(RoundedCornerShape(AppShape.ShapeM))
                        .background(BabyBlue.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = null,
                        modifier = Modifier.size(Dimen.SizeM),
                        tint = SmartBlue
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = AppSpacing.MediumLarge)
                        .weight(1f)
                ) {
                    Text(
                        text = member.fullName,
                        style = MaterialTheme.typography.s16.semiBold(),
                        color = TextPrimaryDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val subtitle = buildString {
                        append(memberTypeText)
                        append(" • ")
                        append(member.identifier)
                        append(" • ")
                        append(member.department)
                    }

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.s12.medium(),
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Registration Status Chip
            val isRegistered = member.linkedUid != null
            val chipBgColor = if (isRegistered) SuccessBackground else Color(0xFFF1F5F9)
            val chipTextColor = if (isRegistered) SuccessGreen else Color(0xFF64748B)
            val statusText = if (isRegistered) {
                stringResource(id = R.string.registered)
            } else {
                stringResource(id = R.string.unregistered)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Dimen.PaddingS)
                    .clip(RoundedCornerShape(AppShape.ShapeXXS))
                    .background(chipBgColor)
                    .padding(horizontal = Dimen.PaddingS, vertical = Dimen.PaddingXXS)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.s12.semiBold(),
                    color = chipTextColor
                )
            }
        }
    }
}
