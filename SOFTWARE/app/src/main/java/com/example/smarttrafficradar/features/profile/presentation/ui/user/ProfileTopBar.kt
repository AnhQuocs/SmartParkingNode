package com.example.smarttrafficradar.features.profile.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.s22
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun ProfileTopBar(
    fullName: String,
    memberType: MemberType,
    identifier: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2, bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(color = SmartBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
                .padding(top = Dimen.PaddingXL)
        ) {
            Text(
                text = stringResource(id = R.string.profile_topbar),
                style = MaterialTheme.typography.s22.semiBold(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(AppSpacing.LPlus))

            MemberInfo(
                fullName = fullName,
                memberType = memberType,
                identifier = identifier
            )
        }
    }
}

@Composable
fun MemberInfo(
    fullName: String,
    memberType: MemberType,
    identifier: String
) {
    val initials = getInitials(fullName)
    val role = if (memberType == MemberType.STUDENT) R.string.student else R.string.employee

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(AppShape.ShapeXL2))
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(color = SmartBlue)
                    .border(3.dp, color = BabyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.s24,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeS))
                            .background(color = BabyBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = role),
                            style = MaterialTheme.typography.s14,
                            lineHeight = 12.sp,
                            color = RoyalPurple,
                            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                        )
                    }

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    Text(
                        text = identifier,
                        style = MaterialTheme.typography.s16,
                        color = SlateGray
                    )
                }
            }
        }
    }
}

private fun getInitials(fullName: String): String {
    val names = fullName.trim().split(" ").filter { it.isNotBlank() }
    return when {
        names.isEmpty() -> ""
        names.size == 1 -> names.first().take(1).uppercase()
        else -> (names.first().take(1) + names.last().take(1)).uppercase()
    }
}