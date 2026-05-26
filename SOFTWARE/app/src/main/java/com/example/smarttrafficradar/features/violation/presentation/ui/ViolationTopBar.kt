package com.example.smarttrafficradar.features.violation.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.RedBackground
import com.example.smarttrafficradar.ui.theme.RedBorder
import com.example.smarttrafficradar.ui.theme.RedPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s24

@Composable
fun ViolationTopBar(size: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(vertical = Dimen.PaddingXXS),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(id = R.string.violations),
                style = MaterialTheme.typography.s24.bold(),
                color = Color.White
            )

            Text(
                text = stringResource(id = R.string.incidents, size),
                style = MaterialTheme.typography.s16,
                color = SlateMist
            )
        }

        Box(
            modifier = Modifier
                .size(Dimen.SizeXXLPlus)
                .clip(RoundedCornerShape(AppShape.ShapeM))
                .border(1.dp, RedBorder, RoundedCornerShape(AppShape.ShapeM))
                .background(color = RedBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = size.toString(),
                style = MaterialTheme.typography.s16,
                color = RedPrimary
            )
        }
    }
}