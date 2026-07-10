package com.example.smarttrafficradar.features.profile.presentation.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.app_system.language.presentation.ui.ChangeLanguageActivity
import com.example.smarttrafficradar.features.app_system.language.presentation.viewmodel.LanguageViewModel
import com.example.smarttrafficradar.features.app_system.settings.presentation.ui.NotificationSettingsActivity
import com.example.smarttrafficradar.features.app_system.settings.presentation.ui.SecuritySettingsActivity
import com.example.smarttrafficradar.features.app_system.settings.presentation.viewmodel.SettingsViewModel
import com.example.smarttrafficradar.features.auth.presentation.ui.ChangePasswordActivity
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.presentation.ui.EditProfileActivity
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AppTitleText
import com.example.smarttrafficradar.ui.theme.AppVersionText
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.withColor

@Composable
fun ProfileScreen(
    profile: UserProfile,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentLang by languageViewModel.currentLanguage.collectAsState()
    val languageText = when (currentLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english)
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese)
    }

    val isNotificationEnabled by settingsViewModel.isNotificationEnabled.collectAsState()
    val notificationText = if (isNotificationEnabled) stringResource(id = R.string.on) else stringResource(id = R.string.off)

    var isShowDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Background)
        ) {
            ProfileTopBar(
                fullName = profile.fullName,
                memberType = profile.memberType,
                identifier = profile.identifier
            )

            PersonalInformationCard(
                memberType = profile.memberType,
                email = profile.email,
                phoneNumber = profile.phoneNumber,
                department = profile.department,
                rfidUid = profile.rfidUid ?: "",
                modifier = Modifier.offset(y = (-32).dp)
            )

            SettingCard(
                languageText = languageText,
                notificationText = notificationText,
                onLanguageClick = {
                    context.startActivity(
                        Intent(
                            context, ChangeLanguageActivity::class.java
                        )
                    )
                },
                onNotificationClick = {
                    context.startActivity(
                        Intent(
                            context, NotificationSettingsActivity::class.java
                        )
                    )
                },
                onSecurityClick = {
                    context.startActivity(
                        Intent(
                            context, SecuritySettingsActivity::class.java
                        )
                    )
                },
                modifier = Modifier
                    .offset(y = (-16).dp)
                    .padding(horizontal = Dimen.PaddingM)
            )

            AccountCard(
                onEditProfile = {
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                },
                onSupportCenter = {
                    context.startActivity(Intent(context, SupportCenterActivity::class.java))
                },
                onChangePassword = {
                    context.startActivity(Intent(context, ChangePasswordActivity::class.java))
                },
                onLogout = { isShowDialog = true },
                modifier = Modifier.padding(horizontal = Dimen.PaddingM)
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.app_name) + " Node",
                    style = MaterialTheme.typography.s16,
                    color = AppTitleText
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = stringResource(R.string.app_version, "1.0.0"),
                    style = MaterialTheme.typography.s14,
                    color = AppVersionText
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.XXL))
        }

        if (isShowDialog) {
            LogoutDialog(onDismiss = { isShowDialog = false }, onConfirm = {
                authViewModel.signOut()
                authViewModel.clearError()
                isShowDialog = false
                navController.navigate("auth") {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
    }
}

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = Color.White, title = {
        Text(
            text = stringResource(R.string.logout_title),
            style = MaterialTheme.typography.s20.withColor(Color.Black)
        )
    }, text = {
        Text(
            text = stringResource(R.string.logout_message),
            style = MaterialTheme.typography.s18,
            color = Color.Black
        )
    }, confirmButton = {
        Button(
            onClick = {
                onConfirm()
                Toast.makeText(
                    context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .padding(horizontal = Dimen.PaddingS)
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoyalBlue
            ),
            shape = RoundedCornerShape(AppShape.ShapeM)
        ) {
            Text(
                text = stringResource(R.string.logout_confirm), color = Color.White
            )
        }
    }, dismissButton = {
        TextButton(
            onClick = onDismiss, modifier = Modifier.padding(horizontal = Dimen.PaddingS)
        ) {
            Text(
                text = stringResource(R.string.cancel), color = RoyalBlue
            )
        }
    }, shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}