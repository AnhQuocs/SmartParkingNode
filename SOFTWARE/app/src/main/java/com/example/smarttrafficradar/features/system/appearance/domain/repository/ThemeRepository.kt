<<<<<<< HEAD
package com.example.smarttrafficradar.features.system.appearance.domain.repository

import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemeConfig(): Flow<ThemeConfig>
    suspend fun setThemeConfig(theme: ThemeConfig)
=======
package com.example.smarttrafficradar.features.system.appearance.domain.repository

import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemeConfig(): Flow<ThemeConfig>
    suspend fun setThemeConfig(theme: ThemeConfig)
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}