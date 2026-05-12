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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanBackground
import com.example.smarttrafficradar.ui.theme.CyanBorder
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.NavyBackground
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s24
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
                .background(color = CyanBackground)
                .border(1.dp, color = CyanBorder, shape = RoundedCornerShape(AppShape.ShapeM)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_control),
                contentDescription = null,
                tint = CyanPrimary,
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
                color = Color.White
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
    val lang = when(selectedLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english) + " 🇬🇧"
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese) + " 🇻🇳"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeM),
        colors = CardDefaults.cardColors(
            containerColor = NavyBackground
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
                    tint = Color.White,
                    modifier = Modifier.size(Dimen.SizeM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.language) + ": ",
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.White
                )

                Text(
                    text = lang,
                    style = MaterialTheme.typography.s16,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            OutlinedButton(
                onClick = { onChangeLanguage() },
                shape = RoundedCornerShape(AppShape.ShapeM),
                border = BorderStroke(1.dp, color = CyanBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.change_language),
                    style = MaterialTheme.typography.s15,
                    color = Color.White
                )
            }
        }
    }
}