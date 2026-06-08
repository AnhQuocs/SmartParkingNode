package com.example.smarttrafficradar.features.auth.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.auth.domain.model.UserRole
import com.example.smarttrafficradar.features.auth.presentation.components.AuthOptions
import com.example.smarttrafficradar.features.auth.presentation.components.AuthOutlinedTextField
import com.example.smarttrafficradar.features.auth.presentation.components.PasswordOutlinedTextField
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthState
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.features.auth.util.AuthValidation
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.TextTertiary
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun SignInScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState = authViewModel.state.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val failedMessage = stringResource(id = R.string.sign_in_failed)
    LaunchedEffect(key1 = uiState.value) {
        when (val state = uiState.value) {
            is AuthState.Success -> {
                val destination = when (state.user.role) {
                    UserRole.ADMIN -> "admin_root"
                    UserRole.USER -> "user_root"
                }

                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }

                authViewModel.clearError()
            }

            is AuthState.Error -> {
                Toast.makeText(context, failedMessage, Toast.LENGTH_SHORT)
                    .show()
                authViewModel.clearError()
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(650.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.auth),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Dimen.PaddingXXL),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeMega)
                )

                Text(
                    text = stringResource(R.string.app_name).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    style = TextStyle(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0D47A1),
                                Color(0xFF1976D2),
                                Color(0xFF26D9E8)
                            )
                        )
                    )
                )

                Text(
                    text = "IoT Parking System",
                    style = MaterialTheme.typography.s16,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp,
                    color = LightPrimary
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter)
        ) {
            SignInForm(
                email = email,
                password = password,
                onEmailChange = { newValue -> email = newValue },
                onPasswordChange = { newValue -> password = newValue },
                onShowDialog = { showDialog = true },
                onSignIn = { email, password ->
                    authViewModel.signIn(email, password)
                }
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            AuthOptions(
                onClick = {

                }
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.dont_have_account) + " ",
                    color = TextTertiary,
                    style = MaterialTheme.typography.s16
                )

                Text(
                    text = stringResource(id = R.string.sign_up) + " ",
                    color = LightPrimary,
                    style = MaterialTheme.typography.s16.semiBold(),
                    modifier = Modifier.clickable {
                        navController.navigate("sign_up") {
                            popUpTo("sign_in") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SignInForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onShowDialog: () -> Unit,
    onSignIn: (String, String) -> Unit
) {
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val showEmailError = emailTouched && !AuthValidation.validateEmail(email)
    val showPasswordError = passwordTouched && !AuthValidation.validatePassword(password)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AuthOutlinedTextField(
            value = email,
            title = stringResource(id = R.string.email_address),
            onValueChange = {
                onEmailChange(it)
                emailTouched = true
            },
            icon = painterResource(id = R.drawable.ic_email),
            placeholder = stringResource(id = R.string.email_placeholder),
            isError = showEmailError,
            errorMessage = if (showEmailError) stringResource(id = R.string.error_invalid_email) else ""
        )

        Spacer(modifier = Modifier.height(Dimen.PaddingM))

        PasswordOutlinedTextField(
            value = password,
            onValueChange = {
                onPasswordChange(it)
                passwordTouched = true
            },
            isError = showPasswordError,
            errorMessage = if (showPasswordError) stringResource(id = R.string.error_password_too_short) else ""
        )

        Spacer(modifier = Modifier.height(Dimen.PaddingSM))

        Text(
            text = stringResource(id = R.string.forgot_password),
            color = ActionDanger,
            style = MaterialTheme.typography.s13,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDialog() }
        )

        Spacer(modifier = Modifier.height(Dimen.PaddingL))

        val isButtonEnable = emailTouched && passwordTouched && !showEmailError && !showPasswordError

        AppButton(
            onClick = { onSignIn(email, password) },
            enabled = isButtonEnable,
            modifier = Modifier.fillMaxWidth(),
            content = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sign_in),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Dimen.SizeM)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    Text(
                        text = stringResource(id = R.string.sign_in),
                        color = Color.White,
                        style = MaterialTheme.typography.s18
                    )
                }
            }
        )
    }
}