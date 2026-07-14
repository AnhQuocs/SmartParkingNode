package com.example.smarttrafficradar.features.management.presentation.ui.registerd_card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListState
import com.example.smarttrafficradar.features.management.presentation.viewmodel.RegistrationListViewModel
import com.example.smarttrafficradar.features.user_profile.domain.model.VehicleType
import com.example.smarttrafficradar.ui.dimens.AppShape
import com.example.smarttrafficradar.ui.dimens.AppSpacing
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.ActionDanger
import com.example.smarttrafficradar.ui.theme.Background
import com.example.smarttrafficradar.ui.theme.InputBackground
import com.example.smarttrafficradar.ui.theme.SlateGray
import com.example.smarttrafficradar.ui.theme.SmartBlue
import com.example.smarttrafficradar.ui.theme.SuccessBackground
import com.example.smarttrafficradar.ui.theme.SuccessGreen
import com.example.smarttrafficradar.ui.theme.TextPrimaryDark
import com.example.smarttrafficradar.ui.theme.TextSecondary
import com.example.smarttrafficradar.ui.theme.TextTertiary
import com.example.smarttrafficradar.utils.s12
import com.example.smarttrafficradar.utils.s14
import com.example.smarttrafficradar.utils.s16
import com.example.smarttrafficradar.utils.semiBold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegisteredCardsScreen(
    onBackClick: () -> Unit,
    onClick: (RegisteredCard) -> Unit,
    registrationListViewModel: RegistrationListViewModel = hiltViewModel()
) {
    val state by registrationListViewModel.state.collectAsState()
    val searchQuery by registrationListViewModel.searchQuery.collectAsState()
    val selectedStatus by registrationListViewModel.selectedStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background)
    ) {
        val totalCount = (state as? RegistrationListState.Success)?.cards?.size ?: 0
        RegisteredCardsTopBar(
            onBackClick = onBackClick,
            count = totalCount
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.M))

            SearchBar(
                query = searchQuery,
                onQueryChange = registrationListViewModel::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            FilterCategories(
                selectedStatus = selectedStatus,
                onStatusSelected = registrationListViewModel::onStatusFilterChange
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val currentState = state) {
                    is RegistrationListState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = SmartBlue
                        )
                    }

                    is RegistrationListState.Error -> {
                        Text(
                            text = currentState.message,
                            color = ActionDanger,
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.s14
                        )
                    }

                    is RegistrationListState.Success -> {
                        if (currentState.filteredCards.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_pending_cards),
                                modifier = Modifier.align(Alignment.Center),
                                color = TextTertiary,
                                style = MaterialTheme.typography.s14
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.M),
                                contentPadding = PaddingValues(bottom = Dimen.PaddingL)
                            ) {
                                items(currentState.filteredCards) { card ->
                                    RegisteredCardItem(
                                        card = card,
                                        onClick = { onClick(card) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightDefault)
            .clip(CircleShape),
        placeholder = {
            Text(
                text = stringResource(id = R.string.search_registered_cards_hint),
                style = MaterialTheme.typography.s14,
                color = TextTertiary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(Dimen.SizeM)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = SmartBlue
        ),
        singleLine = true,
        shape = CircleShape
    )
}

@Composable
private fun FilterCategories(
    selectedStatus: CardStatus?,
    onStatusSelected: (CardStatus?) -> Unit
) {
    val filters = listOf(
        null to stringResource(id = R.string.filter_all),
        CardStatus.ACTIVE to stringResource(id = R.string.filter_active),
        CardStatus.BLOCKED to stringResource(id = R.string.filter_blocked)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        items(filters) { (status, label) ->
            val isSelected = selectedStatus == status
            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onStatusSelected(status) },
                color = if (isSelected) SmartBlue else Color.White,
                shape = CircleShape,
                shadowElevation = if (isSelected) 0.dp else 1.dp
            ) {
                Box(
                    modifier = Modifier.padding(
                        horizontal = Dimen.PaddingML,
                        vertical = Dimen.PaddingXSPlus
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.s14,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisteredCardItem(card: RegisteredCard, onClick: () -> Unit) {
    val vehicleIcon = when (card.vehicleType) {
        VehicleType.CAR -> R.drawable.ic_car
        VehicleType.MOTORBIKE -> R.drawable.ic_motorcycle
    }

    val vehicleTypeStr = when (card.vehicleType) {
        VehicleType.MOTORBIKE -> stringResource(id = R.string.motorcycle)
        VehicleType.CAR -> stringResource(id = R.string.car)
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val registrationDate = if (card.registeredAt != 0L) dateFormatter.format(Date(card.registeredAt)) else "--/--/----"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(AppShape.ShapeXL2),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = InputBackground,
                        shape = RoundedCornerShape(AppShape.ShapeS)
                    ) {
                        Text(
                            text = card.rfidUid,
                            modifier = Modifier.padding(
                                horizontal = AppSpacing.S,
                                vertical = AppSpacing.XXS
                            ),
                            style = MaterialTheme.typography.s12.semiBold(),
                            color = TextPrimaryDark
                        )
                    }

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    val (bgColor, textColor, labelId) = when (card.status) {
                        CardStatus.ACTIVE -> Triple(
                            SuccessBackground,
                            SuccessGreen,
                            R.string.filter_active
                        )

                        CardStatus.BLOCKED -> Triple(
                            ActionDanger.copy(alpha = 0.1f),
                            ActionDanger,
                            R.string.filter_blocked
                        )
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(AppShape.ShapeS)
                    ) {
                        Text(
                            text = stringResource(id = labelId),
                            modifier = Modifier.padding(
                                horizontal = AppSpacing.S,
                                vertical = AppSpacing.XXS
                            ),
                            style = MaterialTheme.typography.s12,
                            color = textColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = card.ownerName,
                    style = MaterialTheme.typography.s16.semiBold(),
                    color = TextPrimaryDark
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = vehicleIcon),
                        contentDescription = null,
                        tint = SlateGray,
                        modifier = Modifier.size(Dimen.SizeS)
                    )

                    Spacer(modifier = Modifier.width(AppSpacing.S))

                    Text(
                        text = vehicleTypeStr,
                        style = MaterialTheme.typography.s14,
                        color = SlateGray
                    )
                }

                Text(
                    text = stringResource(id = R.string.registered_date_label, registrationDate),
                    style = MaterialTheme.typography.s14,
                    color = SlateGray
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(Dimen.SizeSM)
            )
        }
    }
}
