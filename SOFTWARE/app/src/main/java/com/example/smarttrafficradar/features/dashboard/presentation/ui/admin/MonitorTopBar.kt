package com.example.smarttrafficradar.features.dashboard.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.LangUtils
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MonitorTopBar(
    user: AuthUser,
    onClick: () -> Unit,
    onLogout: () -> Unit
) {
    val currentLocale = remember { Locale(LangUtils.currentLang) }
    val datePattern = stringResource(id = R.string.date_pattern)
    val formattedDate = remember(datePattern, currentLocale) {
        SimpleDateFormat(datePattern, currentLocale).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(bottomStart = AppShape.ShapeL, bottomEnd = AppShape.ShapeL))
            .background(SmartBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
                .padding(top = Dimen.PaddingL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AD",
                        style = MaterialTheme.typography.s20.semiBold(),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

                Column(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClick() }
                ) {
                    Text(
                        text = user.username ?: "",
                        style = MaterialTheme.typography.s18.semiBold(),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.XS))

                    Text(
                        text = stringResource(id = R.string.admin_topbar),
                        style = MaterialTheme.typography.s14,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXLPlus)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Column {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.s16,
                    color = Color.White
                )
            }
        }
    }
}
