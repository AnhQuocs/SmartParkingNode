package com.example.smarttrafficradar.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.ui.dimens.Dimen

enum class TabItem(@DrawableRes val iconRes: Int, @StringRes val label: Int) {
    Library(R.drawable.ic_speed, R.string.dashboard),
    Explore(R.drawable.ic_warning, R.string.violations),
    Chart(R.drawable.ic_control, R.string.control),
    Personal(R.drawable.ic_stats, R.string.status)
}

@Composable
fun AppBottomBar(
    currentIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = TabItem.entries.toTypedArray()
    val primaryColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )

        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
        ) {
            // BoxWithConstraints giúp ta lấy được tổng chiều rộng màn hình để chia đều cho các tab
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp) // Trừ hao lề 2 bên
            ) {
                // 1. Tính toán vị trí của ô vuông di chuyển
                val tabWidth = maxWidth / tabs.size
                val indicatorOffset by animateDpAsState(
                    targetValue = tabWidth * currentIndex,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    label = "indicatorOffset"
                )

                // 2. Ô vuông có Border di chuyển theo Tab
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .padding(
                            vertical = 12.dp,
                            horizontal = 6.dp
                        ) // Thu nhỏ ô vuông lại một chút so với tab
                        .border(
                            width = 1.dp,
                            color = primaryColor.copy(alpha = 0.6f), // Border lấy theo theme
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(
                            color = primaryColor.copy(alpha = 0.1f), // Màu nền mờ bên trong ô
                            shape = RoundedCornerShape(16.dp)
                        )
                )

                // 3. Các Icon và Text nằm đè lên trên ô vuông
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, tab ->
                        val selected = currentIndex == index

                        // Animation chuyển màu mượt mà
                        val color by animateColorAsState(
                            targetValue = if (selected) primaryColor else unselectedColor,
                            label = "tabColor"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null // Tắt hiệu ứng ripple mặc định hình tròn
                                ) { onTabSelected(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Bọc Icon trong Box để làm hiệu ứng Glow đè 2 lớp
                                Box(contentAlignment = Alignment.Center) {
                                    // Lớp 1 (Nằm dưới): Bóng phát sáng (Glow)
                                    if (selected) {
                                        Icon(
                                            painter = painterResource(id = tab.iconRes),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(Dimen.SizeM)
                                                .blur(8.dp), // Làm nhòe nét vẽ để tạo ánh sáng tỏa ra
                                            tint = primaryColor.copy(alpha = 0.8f) // Có thể chỉnh alpha để glow sáng/tối tùy ý
                                        )
                                    }

                                    // Lớp 2 (Nằm trên): Icon chính hiển thị sắc nét
                                    Icon(
                                        painter = painterResource(id = tab.iconRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(Dimen.SizeM),
                                        tint = color
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(id = tab.label),
                                    fontSize = 12.sp,
                                    color = color,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}