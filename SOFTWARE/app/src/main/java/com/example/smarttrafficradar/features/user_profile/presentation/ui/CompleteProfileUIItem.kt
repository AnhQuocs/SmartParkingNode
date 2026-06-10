package com.example.smarttrafficradar.features.user_profile.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.LightOnBackground
import com.example.smarttrafficradar.ui.theme.LightOnPrimary
import com.example.smarttrafficradar.ui.theme.LightOnPrimaryContainer
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.LightSurface
import com.example.smarttrafficradar.ui.theme.TextSecondary

@Composable
fun ProfileOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    placeholder: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = LightOnBackground) },
        textStyle = TextStyle(
            color = if (readOnly) LightOnSurface.copy(alpha = 0.8f) else LightOnSurface
        ),
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        supportingText = supportingText,
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = LightPrimary
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = InputBackground,
            disabledTextColor = TextSecondary,
            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedBorderColor = LightPrimary,
            unfocusedBorderColor = Color.LightGray
        )
    )
}

@Composable
fun RoleSelectionItem(
    title: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(AppShape.ShapeL)
    Surface(
        onClick = onClick,
        shape = shape,
        color = LightSurface,
        border = BorderStroke(
            2.dp,
            LightPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingML),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.HeightLarge)
                    .clip(RoundedCornerShape(AppShape.ShapeM)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LightOnPrimary,
                        modifier = Modifier.size(Dimen.SizeL)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimen.PaddingML))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LightOnPrimaryContainer
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightOnPrimaryContainer
                )
            }
        }
    }
}
