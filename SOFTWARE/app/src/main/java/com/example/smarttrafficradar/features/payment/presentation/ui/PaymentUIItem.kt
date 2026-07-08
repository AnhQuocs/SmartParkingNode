package com.example.smarttrafficradar.features.payment.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun PaymentTabs(
    onPayment: () -> Unit,
    onViewHistory: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(AppShape.ShapeL),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingSM),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(AppShape.ShapeL))
                        .background(RoyalBlue)
                        .clickable { onPayment() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_wallet),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Dimen.SizeXL)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Text(
                    text = stringResource(id = R.string.payment),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.debt_payment),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(AppShape.ShapeL),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingSM),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(AppShape.ShapeL))
                        .background(TealGreen)
                        .clickable { onViewHistory() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_history),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(Dimen.SizeXL)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Text(
                    text = stringResource(id = R.string.history),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = stringResource(id = R.string.paid_transactions),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )
            }
        }
    }
}