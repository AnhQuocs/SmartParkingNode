package com.example.smarttrafficradar.features.profile.presentation.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.RoyalPurpleLight
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import com.example.smarttrafficradar.utils.bold
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun PersonalInformationCard(
    email: String,
    phoneNumber: String,
    department: String,
    rfidUid: String,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeXL),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingL)
        ) {
            Text(
                text = stringResource(id = R.string.personal_info),
                style = MaterialTheme.typography.s18.bold(),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            InfoItem(
                iconRes = R.drawable.ic_email,
                label = stringResource(id = R.string.email_address),
                value = email,
                iconColor = RoyalBlue,
                bgColor = RoyalBlueLight
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            InfoItem(
                iconVector = Icons.Default.Phone,
                label = stringResource(id = R.string.phone_number_label),
                value = phoneNumber,
                iconColor = TealGreen,
                bgColor = TealGreenLight
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            InfoItem(
                iconRes = R.drawable.ic_management,
                label = stringResource(id = R.string.department),
                value = department,
                iconColor = RoyalPurple,
                bgColor = RoyalPurpleLight
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            InfoItem(
                iconRes = R.drawable.ic_card,
                label = stringResource(id = R.string.rfid_card_code),
                value = rfidUid,
                iconColor = RoyalBlue,
                bgColor = RoyalBlueLight
            )
        }
    }
}

@Composable
private fun InfoItem(
    label: String,
    value: String,
    iconColor: Color,
    bgColor: Color,
    iconRes: Int? = null,
    iconVector: ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(Dimen.SizeXLPlus)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            }
        }

        Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.s13,
                color = SlateGray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.s16.semiBold(),
                color = Color.Black
            )
        }
    }
}
