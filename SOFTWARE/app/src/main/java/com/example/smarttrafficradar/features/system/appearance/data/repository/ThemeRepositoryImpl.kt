package com.example.smarttrafficradar.features.system.appearance.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import com.example.smarttrafficradar.features.system.appearance.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val THEME_KEY = stringPreferencesKey("theme_config")

class ThemeRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {

    override fun getThemeConfig(): Flow<ThemeConfig> = dataStore.data.map { prefs ->
        val name = prefs[THEME_KEY] ?: ThemeConfig.FOLLOW_SYSTEM.name
        ThemeConfig.valueOf(name)
    }

    override suspend fun setThemeConfig(theme: ThemeConfig) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }
}