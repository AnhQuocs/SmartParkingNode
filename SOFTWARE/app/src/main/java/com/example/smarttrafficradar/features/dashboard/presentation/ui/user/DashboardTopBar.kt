package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.MemberType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun DashboardTopBar(
    fullName: String,
    type: MemberType,
    identifier: String
) {
    val initials = getInitials(fullName)
    val role = if (type == MemberType.STUDENT) R.string.student else R.string.employee

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightXL3)
            .clip(
                shape = RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2,
                    bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(color = SmartBlue),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(color = Color.White)
                    .border(3.dp, color = BabyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.s16,
                    color = LightPrimary
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(18.dp)
                            .clip(RoundedCornerShape(AppShape.ShapeXS))
                            .background(color = BabyBlue.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = role),
                            style = MaterialTheme.typography.s12,
                            lineHeight = 8.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                        )
                    }

                    Spacer(modifier = Modifier.width(AppSpacing.XSPlus))

                    Text(
                        text = identifier,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                painter = painterResource(id = R.drawable.ic_notification2),
                contentDescription = "Notification",
                tint = Color.White,
                modifier = Modifier.size(Dimen.SizeML)
            )
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