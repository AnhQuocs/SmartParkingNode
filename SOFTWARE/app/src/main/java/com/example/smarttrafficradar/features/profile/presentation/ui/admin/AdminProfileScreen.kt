package com.example.smarttrafficradar.features.profile.presentation.ui.admin

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.app_system.language.presentation.ui.ChangeLanguageActivity
import com.example.smarttrafficradar.features.app_system.language.presentation.viewmodel.LanguageViewModel
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.presentation.ui.ChangePasswordActivity
import com.example.smarttrafficradar.features.profile.presentation.ui.user.SettingItem
import com.example.smarttrafficradar.features.profile.presentation.ui.user.SupportCenterActivity
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun AdminProfileScreen(
    user: AuthUser,
    onBackClick: () -> Unit,
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val currentLang by languageViewModel.currentLanguage.collectAsState()
    val languageText = when (currentLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english)
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        user.username?.let { AdminProfileTopBar(userName = it, onBackClick = onBackClick) }

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        AdminActionCard(
            languageText = languageText,
            onChangePassword = {
                context.startActivity(Intent(context, ChangePasswordActivity::class.java))
            },
            onChangeLanguage = {
                context.startActivity(
                    Intent(
                        context, ChangeLanguageActivity::class.java
                    )
                )
            },
            onSupportCenter = {
                context.startActivity(Intent(context, SupportCenterActivity::class.java))
            },
            onTermOfUse = {
                context.startActivity(Intent(context, TermOfUseActivity::class.java))
            },
            onPrivacyPolicy = {
                context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            }
        )
    }
}

@Composable
fun AdminActionCard(
    languageText: String,
    onChangePassword: () -> Unit,
    onChangeLanguage: () -> Unit,
    onSupportCenter: () -> Unit,
    onTermOfUse: () -> Unit,
    onPrivacyPolicy: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = Dimen.PaddingXXS),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.PaddingL)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = Dimen.PaddingSM),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.L)
            ) {
                SettingItem(
                    icon = painterResource(id = R.drawable.ic_language),
                    text = stringResource(id = R.string.language),
                    subText = languageText,
                    onClick = onChangeLanguage
                )

                SettingItem(
                    icon = painterResource(id = R.drawable.ic_key),
                    text = stringResource(id = R.string.change_password),
                    subText = null,
                    onClick = onChangePassword
                )

                SettingItem(
                    icon = painterResource(R.drawable.ic_support),
                    text = stringResource(id = R.string.support_center),
                    subText = null,
                    onClick = onSupportCenter
                )

                AdminSettingItem(
                    icon = Icons.Default.PrivacyTip,
                    text = stringResource(id = R.string.privacy_policy),
                    subText = null,
                    onClick = onPrivacyPolicy
                )

                AdminSettingItem(
                    icon = Icons.Default.Gavel,
                    text = stringResource(id = R.string.terms_of_use),
                    subText = null,
                    onClick = onTermOfUse
                )
            }
        }
    }
}

@Composable
fun AdminSettingItem(
    icon: ImageVector,
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
                imageVector = icon,
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