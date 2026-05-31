package com.example.smarttrafficradar.features.system_monitor.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiPreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveWifiPassword(ssid: String, password: String) {
        val key = stringPreferencesKey("wifi_pwd_$ssid")
        dataStore.edit { preferences ->
            preferences[key] = password
        }
    }

    suspend fun getWifiPassword(ssid: String): String? {
        val key = stringPreferencesKey("wifi_pwd_$ssid")
        return dataStore.data.map { preferences ->
            preferences[key]
        }.firstOrNull()
    }

    suspend fun forgetWifi(ssid: String) {
        val key = stringPreferencesKey("wifi_pwd_$ssid")
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
