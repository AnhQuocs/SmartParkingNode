package com.example.smarttrafficradar.features.dashboard.presentation.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.components.AppButton
import com.example.smarttrafficradar.features.dashboard.presentation.util.toDateString
import com.example.smarttrafficradar.features.dashboard.presentation.util.toTimeString
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingStatus
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.AmberOrange
import com.example.smarttrafficradar.ui.theme.AmberOrangeLight
import com.example.smarttrafficradar.ui.theme.Charcoal
import com.example.smarttrafficradar.ui.theme.GreenBright
import com.example.smarttrafficradar.ui.theme.OceanBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlue
import com.example.smarttrafficradar.ui.theme.RoyalBlueLight
import com.example.smarttrafficradar.ui.theme.RoyalPurple
import com.example.smarttrafficradar.ui.theme.RoyalPurpleLight
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.TealGreen
import com.example.smarttrafficradar.ui.theme.TealGreenLight
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s15
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.s18
import com.example.smarttrafficradar.utils.s22
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun DebtCard(
    currentDebt: String, onPayClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF5F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingML)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.amount_due),
                        style = MaterialTheme.typography.s15,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    Text(
                        text = "$currentDebt ₫",
                        style = MaterialTheme.typography.s22,
                        color = Color(0xFFE7000B)
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = null,
                    tint = Color(0xFFE7000B).copy(alpha = 0.6f),
                    modifier = Modifier.size(Dimen.SizeL)
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            AppButton(
                onClick = { onPayClick() },
                shape = AppShape.ShapeXL,
                enabled = currentDebt != "0",
                content = {
                    Text(
                        text = stringResource(id = R.string.pay_now),
                        color = Color.White,
                        style = MaterialTheme.typography.s16
                    )
                })
        }
    }
}

data class QuickAction(
    val bgrColor: Color, val iconColor: Color, val iconRes: Int, val textRes: Int
)

@Composable
fun QuickActionsSection(
    onRegisterCard: () -> Unit,
    onViewHistory: () -> Unit,
    onPayment: () -> Unit,
    onSupport: () -> Unit
) {
    val quickActions = listOf(
        QuickAction(
            bgrColor = RoyalBlueLight,
            iconColor = RoyalBlue,
            iconRes = R.drawable.ic_card,
            textRes = R.string.register_card
        ), QuickAction(
            bgrColor = TealGreenLight,
            iconColor = TealGreen,
            iconRes = R.drawable.ic_history,
            textRes = R.string.view_history
        ), QuickAction(
            bgrColor = RoyalPurpleLight,
            iconColor = RoyalPurple,
            iconRes = R.drawable.ic_wallet,
            textRes = R.string.payment
        ), QuickAction(
            bgrColor = AmberOrangeLight,
            iconColor = AmberOrange,
            iconRes = R.drawable.ic_support,
            textRes = R.string.support
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Text(
            text = stringResource(id = R.string.quick_actions),
            style = MaterialTheme.typography.s18.semiBold(),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(AppSpacing.M))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val actions = listOf(
                onRegisterCard, onViewHistory, onPayment, onSupport
            )

            quickActions.forEachIndexed { index, action ->
                QuickActionsItem(
                    bgrColor = action.bgrColor,
                    icon = painterResource(action.iconRes),
                    iconColor = action.iconColor,
                    text = stringResource(action.textRes),
                    onClick = actions[index]
                )
            }
        }
    }
}

@Composable
fun QuickActionsItem(
    bgrColor: Color, icon: Painter, iconColor: Color, text: String, onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .width(86.dp)) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = bgrColor),
            shape = RoundedCornerShape(AppShape.ShapeL),
            modifier = Modifier.size(70.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(Dimen.SizeXL)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.S))

        Text(
            text = text, style = MaterialTheme.typography.s12, color = Charcoal
        )
    }
}

data class RecentActivity(
    val textRes: Int, val iconRes: Int, val iconColor: Color, val bgrColor: Color, val fee: Int? = 0
)

@Composable
fun RecentActivitiesSection(
    histories: List<ParkingHistory>,
    onDetail: (String) -> Unit,
    onSeeAll: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.recent_activities),
                style = MaterialTheme.typography.s18.semiBold(),
                color = Color.Black
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onSeeAll() }) {
                Text(
                    text = stringResource(id = R.string.see_all),
                    style = MaterialTheme.typography.s14.semiBold(),
                    color = OceanBlue
                )

                Spacer(modifier = Modifier.width(AppSpacing.XS))

                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = OceanBlue,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.M))

        histories.take(4).forEach { history ->
            val date = history.checkInTime.toDateString()
            val time = history.checkInTime.toTimeString()

            val recentActivity = when (history.status) {
                ParkingStatus.CHECK_IN -> RecentActivity(
                    textRes = R.string.entry_gate,
                    iconRes = R.drawable.ic_arrow_left,
                    iconColor = RoyalBlue,
                    bgrColor = RoyalPurpleLight,
                )

                else -> RecentActivity(
                    textRes = R.string.exit_gate,
                    iconRes = R.drawable.ic_arrow_right,
                    iconColor = TealGreen,
                    bgrColor = TealGreenLight,
                    fee = history.fee
                )
            }

            RecentActivitiesItem(
                date = date,
                time = time,
                recentActivity = recentActivity,
                onClick = { onDetail(history.id) })

            Spacer(modifier = Modifier.height(AppSpacing.M))
        }
    }
}

@Composable
fun RecentActivitiesItem(
    date: String, time: String, recentActivity: RecentActivity, onClick: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightML)
            .clickable { onClick() }) {
        Row(
            modifier = Modifier.padding(Dimen.PaddingSM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXXL)
                    .clip(CircleShape)
                    .background(color = recentActivity.bgrColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = recentActivity.iconRes),
                    contentDescription = null,
                    tint = recentActivity.iconColor,
                    modifier = Modifier.size(Dimen.SizeML)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.M))

            Column(
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = recentActivity.textRes),
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = date, style = MaterialTheme.typography.s12, color = SlateGray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = time, style = MaterialTheme.typography.s15, color = SlateGray
                )

                if (recentActivity.fee != 0) {
                    Spacer(modifier = Modifier.height(AppSpacing.S))

                    Text(
                        text = recentActivity.fee.toString() + " ₫",
                        style = MaterialTheme.typography.s15.semiBold(),
                        color = GreenBright
                    )
                }
            }
        }
    }
}