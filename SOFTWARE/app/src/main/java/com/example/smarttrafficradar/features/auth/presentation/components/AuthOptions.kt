package com.example.smarttrafficradar.features.auth.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.ui.theme.TextTertiary
import com.example.smarttrafficradar.utils.s14

@Composable
fun AuthOptions(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .height(0.5.dp)
                .background(color = SlateMist)
                .weight(1f)
        )

        Text(
            text = stringResource(id = R.string.or),
            color = SlateMist,
            style = MaterialTheme.typography.s14,
            modifier = Modifier
                .padding(horizontal = Dimen.PaddingS)
        )

        Box(
            modifier = Modifier
                .height(0.5.dp)
                .background(color = SlateMist)
                .weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(Dimen.PaddingM))

    Button(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightDefault)
            .padding(horizontal = Dimen.PaddingM),
        colors = ButtonDefaults.buttonColors(
            containerColor = InputBackground
        ),
        shape = RoundedCornerShape(AppShape.ShapeS)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(Dimen.PaddingSM))

            Text(
                text = stringResource(id = R.string.continue_google),
                color = TextTertiary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}