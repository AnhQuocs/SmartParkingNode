package com.example.smarttrafficradar.features.control.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun ControlTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightXL2)
            .clip(
                shape = RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2,
                    bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(color = SmartBlue),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingML)
                .padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(id = R.string.hardware),
                style = MaterialTheme.typography.s20.semiBold(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Text(
                text = stringResource(id = R.string.device_monitoring),
                style = MaterialTheme.typography.s16,
                color = Color.White
            )
        }
    }
}