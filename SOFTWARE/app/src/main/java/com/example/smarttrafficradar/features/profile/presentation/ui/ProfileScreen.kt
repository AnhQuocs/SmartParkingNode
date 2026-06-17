package com.example.smarttrafficradar.features.profile.presentation.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.app_system.language.presentation.ui.ChangeLanguageActivity
import com.example.smarttrafficradar.features.app_system.language.presentation.viewmodel.LanguageViewModel
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.withColor

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    languageViewModel: LanguageViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val currentLang by languageViewModel.currentLanguage.collectAsState()
    val languageText = when (currentLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english)
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese)
    }

    var isShowDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(Dimen.PaddingM)
        ) {
            SettingCard(
                languageText = languageText,
                onLanguageClick = {
                    context.startActivity(
                        Intent(
                            context, ChangeLanguageActivity::class.java
                        )
                    )
                },
                onNotificationClick = {},
                onSecurityClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            AppButton(
                color = Color.White,
                shape = AppShape.ShapeS,
                onClick = { isShowDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.LightGray, RoundedCornerShape(AppShape.ShapeS)),
                content = {
                    Text(
                        text = stringResource(id = R.string.logout_title),
                        color = Color.Red
                    )
                }
            )
            Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
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