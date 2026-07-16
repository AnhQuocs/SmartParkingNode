package com.example.smarttrafficradar.features.profile.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.BabyBlue
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s20
import com.example.smarttrafficradar.utils.s22
import com.example.smarttrafficradar.utils.s24
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun AdminProfileTopBar(
    userName: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(
                RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2, bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(color = SmartBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingM)
                .padding(top = Dimen.PaddingXL)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(Dimen.SizeM)
                        .clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.profile_topbar),
                    style = MaterialTheme.typography.s22.semiBold(),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.LPlus))

            AdminMemberInfo(
                userName = userName
            )
        }
    }
}

@Composable
fun AdminMemberInfo(
    userName: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(AppShape.ShapeXL2))
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(color = SmartBlue)
                    .border(3.dp, color = BabyBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AD",
                    style = MaterialTheme.typography.s24,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.s20.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.system_administrator),
                    style = MaterialTheme.typography.s16,
                    color = SlateGray
                )
            }
        }
    }
}