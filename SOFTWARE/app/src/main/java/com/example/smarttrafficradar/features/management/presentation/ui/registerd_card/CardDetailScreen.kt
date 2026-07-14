package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun CardDetailScreen(
    onBackClick: () -> Unit,
    registeredCard: RegisteredCard
) {
    BackHandler { onBackClick() }

    val status = when (registeredCard.status) {
        CardStatus.ACTIVE -> stringResource(id = R.string.filter_active)
        CardStatus.BLOCKED -> stringResource(id = R.string.filter_blocked)
    }

    val isActive = registeredCard.status == CardStatus.ACTIVE

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
    }
}