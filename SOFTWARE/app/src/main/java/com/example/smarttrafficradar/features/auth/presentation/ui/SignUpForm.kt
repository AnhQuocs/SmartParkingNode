package com.example.smarttrafficradar.features.auth.presentation.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.auth.presentation.components.AuthOutlinedTextField
import com.example.smarttrafficradar.features.auth.presentation.components.PasswordOutlinedTextField
import com.example.smarttrafficradar.features.auth.util.AuthValidation.validateEmail
import com.example.smarttrafficradar.features.auth.util.AuthValidation.validatePassword
import com.example.smarttrafficradar.features.auth.util.AuthValidation.validateUsername
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18

@Composable
fun SignUpForm(
    onSignUp: (String, String, String) -> Unit,
    onSignUpWithAdmin: (String, String, String, String) -> Unit,
    onCheckBoxClick: (Boolean) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var adminCode by remember { mutableStateOf("") }

    var usernameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var adminCodeTouched by remember { mutableStateOf(false) }

    val showUsernameError = usernameTouched && !validateUsername(username)
    val showEmailError = emailTouched && !validateEmail(email)
    val showPasswordError = passwordTouched && !validatePassword(password)

    var isAdmin by remember { mutableStateOf(false) }

    AuthOutlinedTextField(
        icon = painterResource(id = R.drawable.ic_person),
        value = username,
        onValueChange = {
            username = it
            usernameTouched = true
        },
        title = stringResource(id = R.string.username),
        placeholder = stringResource(id = R.string.username_placeholder),
        isError = showUsernameError,
        errorMessage = if (showUsernameError) stringResource(id = R.string.error_username_too_short) else ""
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    AuthOutlinedTextField(
        icon = painterResource(id = R.drawable.ic_email),
        value = email,
        onValueChange = {
            email = it
            emailTouched = true
        },
        title = stringResource(id = R.string.email_address),
        placeholder = stringResource(id = R.string.email_placeholder),
        isError = showEmailError,
        errorMessage = if (showEmailError) stringResource(id = R.string.error_invalid_email) else ""
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    PasswordOutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            passwordTouched = true
        },
        isError = showPasswordError,
        errorMessage = if (showPasswordError) stringResource(id = R.string.error_password_too_short) else "",
    )

    Spacer(modifier = Modifier.height(Dimen.PaddingSM))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = isAdmin,
            onCheckedChange = { isChecked ->
                isAdmin = isChecked
                if (!isChecked) adminCode = ""
                onCheckBoxClick(isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = LightPrimary,
                uncheckedColor = SlateMist,
                checkmarkColor = Color.White
            ),
            modifier = Modifier.padding(0.dp)
        )
        Text(
            text = stringResource(id = R.string.register_admin),
            style = MaterialTheme.typography.s16,
            color = Color.Black,
            modifier = Modifier.clickable { isAdmin = !isAdmin }
        )
    }

    AnimatedVisibility(visible = isAdmin) {
        Column {
            Spacer(modifier = Modifier.height(4.dp))

            AuthOutlinedTextField(
                value = adminCode,
                onValueChange = {
                    adminCode = it
                    adminCodeTouched = true
                },
                title = stringResource(id = R.string.admin_code),
                placeholder = stringResource(id = R.string.admin_code_placeholder),
                icon = painterResource(id = R.drawable.ic_key),
                isError = adminCodeTouched && adminCode.isBlank(),
                errorMessage = if (adminCodeTouched && adminCode.isBlank()) stringResource(id = R.string.code_is_required) else "",
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))
        }
    }

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    val isFormTouched =
        usernameTouched && emailTouched && passwordTouched

    val hasError =
        showUsernameError || showEmailError || showPasswordError

    val isSignUpEnable = isFormTouched && !hasError

    val isSignUpAdminEnable = adminCode.isNotBlank() && !hasError

    AppButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            if (isAdmin) {
                onSignUpWithAdmin(username, email, password, adminCode)
            } else {
                onSignUp(username, email, password)
            }
        },
        enabled = if (isAdmin) isSignUpAdminEnable else isSignUpEnable,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sign_up),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimen.SizeM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.S))

                Text(
                    text = if (!isAdmin)
                        stringResource(id = R.string.sign_up)
                    else stringResource(
                        id = R.string.sign_up_admin
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.s18
                )
            }
        }
    )
}