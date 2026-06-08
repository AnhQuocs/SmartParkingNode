package com.example.smarttrafficradar.features.auth.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.withColor
import kotlin.math.sign

@Composable
fun AuthOutlinedTextField(
    title: String,
    placeholder: String,
    value: String,
    icon: Painter,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            label = {
                Text(text = title, style = MaterialTheme.typography.s15.withColor(Color.Black))
            },
            placeholder = {
                Text(text = placeholder, style = MaterialTheme.typography.s15.withColor(SlateMist))
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
                    painter = icon,
                    contentDescription = title,
                    tint = SlateMist,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            },
            singleLine = true,
            isError = isError,
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