package com.example.smarttrafficradar.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    shape: Dp = AppShape.ShapeS,
    color: Color = LightPrimary,
) {
    Button(
        onClick = {
            onClick()
        },
        enabled = enabled,
        modifier = modifier
            .height(Dimen.HeightDefault)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(shape),
    ) {
        content()
    }
}