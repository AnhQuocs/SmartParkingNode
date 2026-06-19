@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.smarttrafficradar.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.ui.dimens.Dimen
import com.example.smarttrafficradar.ui.theme.LightPrimary
import com.example.smarttrafficradar.ui.theme.SlateMist

data class BottomBarItem(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
)

@Composable
fun AppBottomBar(
    tabs: List<BottomBarItem>,
    currentIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
    ) {
        Surface(
            shadowElevation = 8.dp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(80.dp)
            ) {
                tabs.forEachIndexed { index, tab ->
                    val selected = currentIndex == index

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(index) },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = tab.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(Dimen.SizeM),
                                colorFilter = if (selected) ColorFilter.tint(LightPrimary) else ColorFilter.tint(SlateMist)
                            )
                            Text(
                                text = stringResource(id = tab.labelRes),
                                fontSize = 12.sp,
                                color = if (selected) LightPrimary else SlateMist
                            )
                        }
                    }
                }
            }
        }
    }
}