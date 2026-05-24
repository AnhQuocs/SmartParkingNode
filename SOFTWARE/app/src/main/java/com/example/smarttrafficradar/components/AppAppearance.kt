package com.example.smarttrafficradar.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import com.example.smarttrafficradar.ui.theme.SmartTrafficRadarTheme

@Composable
fun AppAppearance(
    themeConfig: ThemeConfig,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    SmartTrafficRadarTheme(themeConfig = themeConfig) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            content = content
        )
    }
}