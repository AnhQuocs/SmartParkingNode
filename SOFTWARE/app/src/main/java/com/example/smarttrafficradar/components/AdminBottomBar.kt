package com.example.smarttrafficradar.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.example.smarttrafficradar.R

enum class AdminTabItem(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    Monitor(R.drawable.ic_monitor, R.string.monitor),
    Management(R.drawable.ic_management, R.string.management),
    Hardware(R.drawable.ic_hardware, R.string.hardware),
    Analytics(R.drawable.ic_analytics, R.string.analytics);

    companion object {
        val bottomBarItems = AdminTabItem.entries.map {
            BottomBarItem(it.iconRes, it.labelRes)
        }
    }
}

@Composable
fun AdminBottomBar(
    currentIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    AppBottomBar(
        tabs = AdminTabItem.bottomBarItems,
        currentIndex = currentIndex,
        onTabSelected = onTabSelected
    )
}