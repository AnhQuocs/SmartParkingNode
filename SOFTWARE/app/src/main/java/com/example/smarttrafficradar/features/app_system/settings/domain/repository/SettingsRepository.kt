package com.example.smarttrafficradar.features.app_system.settings.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
}
