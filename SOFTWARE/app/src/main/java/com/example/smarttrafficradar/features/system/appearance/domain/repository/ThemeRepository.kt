package com.example.smarttrafficradar.features.system.appearance.domain.repository

import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemeConfig(): Flow<ThemeConfig>
    suspend fun setThemeConfig(theme: ThemeConfig)
}