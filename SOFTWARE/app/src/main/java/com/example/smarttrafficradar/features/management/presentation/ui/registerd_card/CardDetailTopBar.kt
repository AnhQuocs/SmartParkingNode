package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun CardDetailTopBar(
    rfidUid: String,
    onBackClick: () -> Unit
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingML)
                .padding(horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXLPlus)
                    .clip(CircleShape)
                    .background(InputBackground.copy(alpha = 0.2f))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingM)
            ) {
                Text(
                    text = stringResource(id = R.string.card_detail),
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = Color.White
                )

                Text(
                    text = rfidUid,
                    style = MaterialTheme.typography.s16,
                    color = Color.White
                )
            }
        }
    }
}