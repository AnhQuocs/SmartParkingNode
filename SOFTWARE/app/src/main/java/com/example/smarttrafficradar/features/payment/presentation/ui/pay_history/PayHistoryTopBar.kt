package com.example.smarttrafficradar.features.payment.presentation.ui.pay_history

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.payment.domain.usecase.PaymentSummary
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun PayHistoryTopBar(
    onBack: () -> Unit,
    paymentSummary: PaymentSummary
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
                        .clickable { onBack() }
                )

                Spacer(modifier = Modifier.width(AppSpacing.MediumLarge))

                Text(
                    text = stringResource(id = R.string.payment_topbar_title),
                    style = MaterialTheme.typography.s18.semiBold(),
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppShape.ShapeL),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(Dimen.PaddingM)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(id = R.string.total_paid),
                                style = MaterialTheme.typography.s14,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(AppSpacing.S))

                            val annotatedString = buildAnnotatedString {
                                append(formatFee(paymentSummary.totalPaid))
                                append(" ")
                                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append("đ")
                                }
                            }

                            Text(
                                text = annotatedString,
                                style = MaterialTheme.typography.s18.semiBold(),
                                color = GreenBright,
                                fontSize = 28.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GreenBright.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_wallet),
                                contentDescription = null,
                                tint = GreenBright,
                                modifier = Modifier.size(Dimen.SizeM)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimen.PaddingM))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(Dimen.PaddingM))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.total_transactions),
                            style = MaterialTheme.typography.s14,
                            color = Color.Gray
                        )
                        Text(
                            text = stringResource(id = R.string.transactions_count, paymentSummary.totalTransactions),
                            style = MaterialTheme.typography.s14.semiBold(),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

private fun formatFee(fee: Int): String {
    return String.format("%,d", fee).replace(',', '.')
}
