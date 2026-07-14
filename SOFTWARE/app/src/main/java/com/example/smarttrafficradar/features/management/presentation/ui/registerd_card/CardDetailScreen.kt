package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun CardDetailScreen(
    onBackClick: () -> Unit,
    registeredCard: RegisteredCard,
    onBlock: (String) -> Unit,
    onActive: (String) -> Unit
) {
    BackHandler { onBackClick() }

    var showBlockDialog by remember { mutableStateOf(false) }

    val status = when (registeredCard.status) {
        CardStatus.ACTIVE -> stringResource(id = R.string.filter_active)
        CardStatus.BLOCKED -> stringResource(id = R.string.filter_blocked)
    }

    val isActive = registeredCard.status == CardStatus.ACTIVE

    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.confirm_block_title),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = TextPrimaryDark
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.confirm_block_message),
                    style = MaterialTheme.typography.s15,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBlock(registeredCard.id)
                        showBlockDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.lock_card),
                        color = ActionDanger
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = TextSecondary
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(AppShape.ShapeL)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        CardDetailTopBar(
            rfidUid = registeredCard.rfidUid,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimen.PaddingM),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.card_status),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Box(
                    modifier = Modifier
                        .height(25.dp)
                        .clip(RoundedCornerShape(AppShape.ShapeM))
                        .background(
                            if (isActive) GreenBright.copy(alpha = 0.2f) else ActionDanger.copy(
                                alpha = 0.2f
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.s15,
                        color = if (isActive) GreenBright else ActionDanger,
                        modifier = Modifier.padding(horizontal = Dimen.PaddingS)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        CardInformation(card = registeredCard)

        Spacer(modifier = Modifier.weight(1f))

        StatusAction(
            isActive = isActive,
            onAction = {
                if (isActive) {
                    showBlockDialog = true
                } else {
                    onActive(registeredCard.id)
                }
            }
        )
        
        Spacer(modifier = Modifier.height(Dimen.PaddingL))
    }
}

@Composable
fun StatusAction(
    isActive: Boolean,
    onAction: () -> Unit
) {
    if (isActive) {
        OutlinedButton(
            onClick = onAction,
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ActionDanger.copy(alpha = 0.1f)
            ),
            border = BorderStroke(1.dp, ActionDanger),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier
                    .padding(Dimen.PaddingS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    tint = ActionDanger,
                    modifier = Modifier.size(Dimen.SizeM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.lock_card),
                    style = MaterialTheme.typography.s16,
                    color = ActionDanger
                )
            }
        }
    } else {
        Button(
            onClick = onAction,
            shape = RoundedCornerShape(AppShape.ShapeL),
            colors = ButtonDefaults.buttonColors(
                containerColor = GreenBright.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Row(
                modifier = Modifier
                    .padding(Dimen.PaddingS),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = null,
                    tint = GreenBright,
                    modifier = Modifier.size(Dimen.SizeM)
                )

                Spacer(modifier = Modifier.width(AppSpacing.M))

                Text(
                    text = stringResource(id = R.string.activate_card),
                    style = MaterialTheme.typography.s16,
                    color = GreenBright
                )
            }
        }
    }
}
