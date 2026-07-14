package com.example.smarttrafficradar.features.control.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.NavyBackground
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun LanguagesCard(selectedLang: AppLanguage, onChangeLanguage: () -> Unit) {
    val lang = when (selectedLang) {
        AppLanguage.ENGLISH -> stringResource(id = R.string.english) + " 🇬🇧"
        AppLanguage.VIETNAMESE -> stringResource(id = R.string.vietnamese) + " 🇻🇳"
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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