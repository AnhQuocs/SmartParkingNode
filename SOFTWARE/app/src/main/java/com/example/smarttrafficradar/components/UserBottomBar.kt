package com.example.smarttrafficradar.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.example.smarttrafficradar.R

enum class UserTabItem(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    Dashboard(R.drawable.ic_dashboard, R.string.dashboard),
    History(R.drawable.ic_history, R.string.history),
    Payment(R.drawable.ic_payment, R.string.payment),
    Profile(R.drawable.ic_profile, R.string.profile);

    companion object {
        val bottomBarItems = entries.map {
            BottomBarItem(it.iconRes, it.labelRes)
        }
    }
}

@Composable
fun UserBottomBar(
    currentIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    AppBottomBar(
        tabs = UserTabItem.bottomBarItems,
        currentIndex = currentIndex,
        onTabSelected = onTabSelected
    )
}