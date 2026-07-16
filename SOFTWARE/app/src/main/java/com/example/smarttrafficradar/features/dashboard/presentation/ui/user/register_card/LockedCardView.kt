package com.example.smarttrafficradar.features.dashboard.presentation.ui.user.register_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ErrorRed
import com.example.smarttrafficradar.ui.theme.ErrorRedLight
import com.example.smarttrafficradar.ui.theme.LightOnSurface
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun LockedCardView(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(Dimen.PaddingL),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .background(ErrorRedLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lock),
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                Text(
                    text = stringResource(id = R.string.card_locked_title),
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = LightOnSurface
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.card_locked_desc),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(AppSpacing.XL))

                UnlockGuideSection()

                Spacer(modifier = Modifier.height(AppSpacing.XL))

                OfficeInfoSection()
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.L))

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimen.HeightLarge),
            shape = RoundedCornerShape(AppShape.ShapeXXL),
            colors = ButtonDefaults.buttonColors(containerColor = SmartBlue)
        ) {
            Text(
                text = stringResource(id = R.string.back_to_dashboard),
                style = MaterialTheme.typography.s16.semiBold()
            )
        }
    }
}

@Composable
fun UnlockGuideSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.unlock_guide_title),
            style = MaterialTheme.typography.s16.semiBold(),
            color = LightOnSurface
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))

        GuideItem(number = "1", text = stringResource(id = R.string.unlock_step_1))
        GuideItem(number = "2", text = stringResource(id = R.string.unlock_step_2))
        GuideItem(number = "3", text = stringResource(id = R.string.unlock_step_3))
    }
}
