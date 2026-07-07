package com.example.smarttrafficradar.features.payment.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun PaymentTopBar(
    currentDebt: Int,
    paid: Int,
    lastPaidAt: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = AppShape.ShapeXL2,
                    bottomEnd = AppShape.ShapeXL2
                )
            )
            .background(SmartBlue)
            .padding(bottom = Dimen.PaddingML)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimen.PaddingXL)
                .padding(Dimen.PaddingM)
        ) {
            Text(
                text = stringResource(id = R.string.payment_topbar_title),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.White,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Dimen.PaddingML),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PaymentSummaryItem(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.payment_amount_due),
                        value = "${formatFee(currentDebt)} đ",
                        valueColor = Color(0xFFFF3B30) // Red color for debt
                    )

                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    PaymentSummaryItem(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.payment_total_paid),
                        value = "${formatFee(paid)} đ",
                        valueColor = GreenBright
                    )

                    VerticalDivider(
                        modifier = Modifier.height(40.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    PaymentSummaryItem(
                        modifier = Modifier.weight(1f),
                        label = stringResource(id = R.string.payment_last_paid),
                        value = lastPaidAt,
                        valueColor = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentSummaryItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.s13,
            color = Color.Gray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(AppSpacing.XS))
        Text(
            text = value,
            style = MaterialTheme.typography.s14.semiBold(),
            color = valueColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun formatFee(fee: Int): String {
    return String.format("%,d", fee).replace(',', '.')
}
