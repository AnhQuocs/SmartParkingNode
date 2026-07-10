package com.example.smarttrafficradar.features.profile.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun SettingCard(
    languageText: String,
    notificationText: String,
    onLanguageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onSecurityClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimen.PaddingXXS),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingL)
        ) {
            Text(
                text = stringResource(id = R.string.setting),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            Column(
                modifier = Modifier.padding(horizontal = Dimen.PaddingSM),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.L)
            ) {
                SettingItem(
                    icon = painterResource(id = R.drawable.ic_language),
                    text = stringResource(id = R.string.language),
                    subText = languageText,
                    onClick = onLanguageClick
                )

                SettingItem(
                    icon = painterResource(id = R.drawable.ic_notification2),
                    text = stringResource(id = R.string.notification),
                    subText = notificationText,
                    onClick = onNotificationClick
                )

                SettingItem(
                    icon = painterResource(id = R.drawable.ic_security),
                    text = stringResource(id = R.string.security),
                    subText = null,
                    onClick = onSecurityClick
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: Painter,
    text: String,
    subText: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = Dimen.PaddingXXS),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(Dimen.SizeM)
            )

            Spacer(modifier = Modifier.width(AppSpacing.M))

            Text(
                text = text,
                style = MaterialTheme.typography.s16.semiBold(),
                color = Color.Black
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (subText != null) {
                Text(
                    text = subText,
                    style = MaterialTheme.typography.s16,
                    color = SlateGray
                )
                Spacer(modifier = Modifier.width(AppSpacing.M))
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(Dimen.SizeS)
            )
        }
    }
}
