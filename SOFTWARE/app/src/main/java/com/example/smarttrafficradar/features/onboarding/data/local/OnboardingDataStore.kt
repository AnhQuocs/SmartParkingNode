package com.example.smarttrafficradar.features.onboarding.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

object OnboardingPrefKeys {
    val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
}

class OnboardingDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun setOnboardingDone(done: Boolean) {
        dataStore.edit { prefs ->
            prefs[OnboardingPrefKeys.ONBOARDING_DONE] = done
        }
    }

    suspend fun isOnboardingDone(): Boolean {
        return dataStore.data
            .map { it[OnboardingPrefKeys.ONBOARDING_DONE] ?: false }
            .first()
    }
}