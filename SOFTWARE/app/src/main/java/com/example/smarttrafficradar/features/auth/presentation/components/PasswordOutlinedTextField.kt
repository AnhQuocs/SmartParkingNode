package com.example.smarttrafficradar.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.withColor

@Composable
fun PasswordOutlinedTextField(
    value: String,
    isError: Boolean,
    errorMessage: String,
    onValueChange: (String) -> Unit
) {
    var isShowPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = {
                Text(
                    text = stringResource(id = R.string.password),
                    style = MaterialTheme.typography.s15.withColor(Color.Black)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.password_placeholder),
                    style = MaterialTheme.typography.s15.withColor(SlateMist)
                )
            },
            textStyle = MaterialTheme.typography.s14.withColor(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                errorContainerColor = InputBackground,
                errorBorderColor = Color.Red,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(AppShape.ShapeM),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_password),
                    contentDescription = null,
                    tint = SlateMist,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { isShowPassword = !isShowPassword }
                ) {
                    Icon(
                        if (isShowPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        tint = SlateMist,
                        contentDescription = null
                    )
                }
            },
            singleLine = true,
            isError = isError,
            visualTransformation = if (!isShowPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            modifier = Modifier.fillMaxWidth().height(Dimen.HeightLarge)
        )

        if (isError && errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}