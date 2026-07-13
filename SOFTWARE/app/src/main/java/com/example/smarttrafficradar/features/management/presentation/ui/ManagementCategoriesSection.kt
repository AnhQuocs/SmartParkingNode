package com.example.smarttrafficradar.features.management.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.CyanPrimary
import com.example.smarttrafficradar.ui.theme.IndigoPrimary
import com.example.smarttrafficradar.ui.theme.OrangePrimary
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold

@Composable
fun ManagementCategoriesSection(
    onRegistrationRequests: () -> Unit,
    onRegisteredCardsClick: () -> Unit,
    onUserListClick: () -> Unit,
    registrationRequests: List<RegistrationRequest>,
    registeredCards: List<RegisteredCard>,
    users: List<OrganizationMember>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingM),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
    ) {
        ManagementCategoryItem(
            icon = R.drawable.ic_card,
            iconColor = OrangePrimary,
            text = stringResource(id = R.string.pending_registrations),
            onClick = onRegistrationRequests,
            count = registrationRequests.size
        )

        ManagementCategoryItem(
            icon = R.drawable.ic_check,
            iconColor = CyanPrimary,
            text = stringResource(id = R.string.registered_cards),
            onClick = onRegisteredCardsClick,
            count = registeredCards.size
        )

        ManagementCategoryItem(
            icon = R.drawable.ic_people,
            iconColor = IndigoPrimary,
            text = stringResource(id = R.string.users),
            onClick = onUserListClick,
            count = users.size
        )
    }
}

@Composable
fun ManagementCategoryItem(
    icon: Int,
    iconColor: Color,
    text: String,
    count: Int,
    onClick: () -> Unit
) {
    val countText = pluralStringResource(
        R.plurals.item_count,
        count,
        count
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(AppShape.ShapeL)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .padding(vertical = Dimen.PaddingSM, horizontal = Dimen.PaddingM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXXL)
                    .clip(RoundedCornerShape(AppShape.ShapeL))
                    .background(color = iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(Dimen.SizeML)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.L))

            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = countText,
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = SlateGray,
                modifier = Modifier.size(Dimen.SizeSM)
            )
        }
    }
}
