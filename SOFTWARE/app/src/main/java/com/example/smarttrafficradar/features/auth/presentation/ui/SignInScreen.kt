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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.smarttrafficradar.features.auth.domain.model.UserRole
import com.example.smarttrafficradar.features.auth.domain.model.UserStatus
import com.example.smarttrafficradar.features.auth.presentation.components.AuthOptions
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthState
import com.example.smarttrafficradar.features.auth.presentation.viewmodel.AuthViewModel
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.TextTertiary
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold
import com.example.smarttrafficradar.utils.withColor

@Composable
fun SignInScreen(
    navController: NavController, authViewModel: AuthViewModel = hiltViewModel()
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
                val destination = when {
                    state.user.role == UserRole.ADMIN -> "admin_root"
                    state.user.status == UserStatus.PROFILE_INCOMPLETE -> "profile_completion_root"
                    else -> "user_root"
                }

                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }

                authViewModel.clearError()
            }

            is AuthState.Error -> {
                Toast.makeText(context, failedMessage, Toast.LENGTH_SHORT).show()
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
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.auth),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingXXL),
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
                                Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF26D9E8)
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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimen.PaddingUltra)
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_back),
                    style = MaterialTheme.typography.s24.withColor(Color.Black)
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.sign_in_hint),
                    style = MaterialTheme.typography.s16.withColor(Color.Black)
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
                            Color.Transparent, Color.White
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .padding(bottom = Dimen.PaddingXXL)
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
                })

            Spacer(modifier = Modifier.height(Dimen.PaddingL))

            AuthOptions(
                onClick = {

                })

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
                    })
            }
        }

        if (uiState.value == AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.2f))
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    }, contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LightPrimary)
            }
        }
    }
}