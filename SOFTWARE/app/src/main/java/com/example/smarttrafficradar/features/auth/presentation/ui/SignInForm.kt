package com.example.smarttrafficradar.features.auth.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.auth.presentation.components.AuthOutlinedTextField
import com.example.smarttrafficradar.features.auth.presentation.components.PasswordOutlinedTextField
import com.example.smarttrafficradar.features.auth.util.AuthValidation
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s18

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
                .clickable { onShowDialog() })

        Spacer(modifier = Modifier.height(Dimen.PaddingL))

        val isButtonEnable =
            emailTouched && passwordTouched && !showEmailError && !showPasswordError

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