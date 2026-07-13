package com.example.smarttrafficradar.features.management.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.presentation.viewmodel.PendingCardsState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.PendingCardsViewModel
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s13
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UnpaidCardSection(
    viewModel: PendingCardsViewModel,
    state: PendingCardsState,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingS, horizontal = Dimen.PaddingM)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Dimen.PaddingM, vertical = Dimen.PaddingSM)
        ) {
            Icon(
                imageVector = Icons.Default.CreditCardOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(Dimen.SizeM)
            )
            Spacer(modifier = Modifier.width(AppSpacing.S))
            Text(
                text = stringResource(R.string.unpaid_cards_title),
                style = MaterialTheme.typography.s15.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        when (state) {
            is PendingCardsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimen.HeightXL3),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = Dimen.PaddingXXS)
                }
            }

            is PendingCardsState.Success -> {
                if (state.cards.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimen.PaddingXL),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_pending_cards),
                            style = MaterialTheme.typography.s14,
                            color = SlateGray
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingM)
                    ) {
                        state.cards.take(3).forEach { card ->
                            PendingCardItem(
                                card = card,
                                onApprove = { viewModel.approveCard(card.uid) },
                                onReject = { viewModel.rejectCard(card.uid) }
                            )

                            Spacer(modifier = Modifier.height(AppSpacing.M))
                        }
                    }
                }
            }

            is PendingCardsState.Error -> {
                Text(
                    text = stringResource(R.string.error_loading_pending, state.message),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(Dimen.PaddingM)
                )
            }
        }
    }
}

@Composable
private fun PendingCardItem(
    card: PendingCard,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(card.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .background(color = Color.White)
            .border(1.5.dp, color = InputBackground, shape = RoundedCornerShape(AppShape.ShapeL))
    ) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // UID Capsule
                Surface(
                    color = InputBackground,
                    shape = RoundedCornerShape(AppShape.ShapeXXL),
                    modifier = Modifier.padding(bottom = AppSpacing.S)
                ) {
                    Text(
                        text = card.uid,
                        color = Color.Black,
                        style = MaterialTheme.typography.s13.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(
                            horizontal = Dimen.PaddingS,
                            vertical = Dimen.PaddingXXS
                        )
                    )
                }

                // Time Row with Clock Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(Dimen.SizeS),
                        tint = SlateGray
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.XS))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.s13,
                        color = SlateGray
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                // User Info in Blue
                Text(
                    text = "${card.fullName} - ${card.identifier}",
                    style = MaterialTheme.typography.s14.copy(fontWeight = FontWeight.Medium),
                    color = RoyalBlue
                )

                Spacer(modifier = Modifier.height(AppSpacing.XSPlus))

                // Reason
                Text(
                    text = card.reason,
                    style = MaterialTheme.typography.s13,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = AppSpacing.XXS)
                )
            }

            // Buttons Column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.XS)
            ) {
                Button(
                    onClick = onApprove,
                    contentPadding = PaddingValues(horizontal = Dimen.PaddingSM),
                    modifier = Modifier.height(Dimen.HeightSmall),
                    shape = RoundedCornerShape(AppShape.ShapeS)
                ) {
                    Text(
                        text = stringResource(R.string.approve),
                        style = MaterialTheme.typography.s13
                    )
                }

                OutlinedButton(
                    onClick = onReject,
                    contentPadding = PaddingValues(horizontal = Dimen.PaddingSM),
                    modifier = Modifier.height(Dimen.HeightSmall),
                    shape = RoundedCornerShape(AppShape.ShapeS),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(
                        Dimen.PaddingXXS,
                        MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.reject),
                        style = MaterialTheme.typography.s13
                    )
                }
            }
        }
    }
}
