package com.example.smarttrafficradar.features.control

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanBorder
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.NavyBackground
import com.example.smarttrafficradar.ui.theme.OrangePrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.YellowBackground
import com.example.smarttrafficradar.ui.theme.YellowBorder
import com.example.smarttrafficradar.ui.theme.YellowPrimary
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.s32
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun ControlTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimen.SizeXXLPlus)
                .clip(RoundedCornerShape(AppShape.ShapeM))
                .background(color = CyanPrimary.copy(alpha = 0.1f))
                .border(
                    1.dp,
                    color = CyanPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(AppShape.ShapeM)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_control),
                contentDescription = null,
                tint = CyanBorder,
                modifier = Modifier.size(Dimen.SizeXLPlus)
            )
        }

        Spacer(modifier = Modifier.width(AppSpacing.S))

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = Dimen.PaddingXXS),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.control_panel),
                style = MaterialTheme.typography.s24.bold(),
                color = NavyBackground
            )

            Text(
                text = stringResource(id = R.string.system_configuration),
                style = MaterialTheme.typography.s16,
                color = SlateMist
            )
        }
    }
}

@Composable
fun LanguagesCard(selectedLang: AppLanguage, onChangeLanguage: () -> Unit) {
    val lang = when (selectedLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english) + " 🇬🇧"
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese) + " 🇻🇳"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_language),
                    contentDescription = null,
                    tint = NavyBackground,
                    modifier = Modifier.size(Dimen.SizeM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.language) + ": ",
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = NavyBackground
                )

                Text(
                    text = lang,
                    style = MaterialTheme.typography.s16,
                    color = SlateMist
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            OutlinedButton(
                onClick = { onChangeLanguage() },
                shape = RoundedCornerShape(AppShape.ShapeM),
                border = BorderStroke(1.dp, color = NavyBackground.copy(alpha = 0.12f)),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBackground)
            ) {
                Text(
                    text = stringResource(id = R.string.change_language),
                    style = MaterialTheme.typography.s15.semiBold()
                )
            }
        }
    }
}

@Composable
fun SpeedLimitCard(
    currentThreshold: Int,
    onThresholdChange: (Int) -> Unit
) {
    var sliderValue by remember(currentThreshold) { mutableFloatStateOf(currentThreshold.toFloat()) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingSM)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimen.SizeXXL)
                        .clip(RoundedCornerShape(AppShape.ShapeM))
                        .background(color = YellowPrimary.copy(alpha = 0.1f))
                        .border(
                            1.dp,
                            color = YellowPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(AppShape.ShapeM)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_radar_speed),
                        contentDescription = null,
                        tint = YellowPrimary,
                        modifier = Modifier.size(Dimen.SizeL)
                    )
                }

                Spacer(modifier = Modifier.width(AppSpacing.S))

                Column {
                    Text(
                        text = stringResource(id = R.string.speed_limit_threshold),
                        style = MaterialTheme.typography.s18.bold(),
                        color = NavyBackground
                    )
                    Text(
                        text = stringResource(id = R.string.trigger_violations_desc),
                        style = MaterialTheme.typography.s15,
                        color = SlateMist
                    )
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = sliderValue.toInt().toString(),
                    style = MaterialTheme.typography.s32.copy(fontSize = 40.sp).bold(),
                    color = OrangePrimary
                )
                Spacer(modifier = Modifier.width(AppSpacing.XS))
                Text(
                    text = stringResource(id = R.string.kmh_unit),
                    style = MaterialTheme.typography.s18.bold(),
                    color = SlateMist,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = {
                    onThresholdChange(sliderValue.toInt())
                },
                valueRange = 20f..120f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = OrangePrimary,
                    activeTrackColor = OrangePrimary,
                    inactiveTrackColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "20 " + stringResource(id = R.string.kmh_unit),
                    style = MaterialTheme.typography.s15,
                    color = SlateMist
                )
                Text(
                    text = "120 " + stringResource(id = R.string.kmh_unit),
                    style = MaterialTheme.typography.s15,
                    color = SlateMist
                )
            }
        }
    }
}

@Composable
fun SetupNetworkButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        border = BorderStroke(1.dp, color = Color.Black.copy(alpha = 0.2f)),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Icon(Icons.Default.Settings, contentDescription = null, tint = OrangePrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.setup_network),
            style = MaterialTheme.typography.s16.bold()
        )
    }
}
