package com.example.smarttrafficradar.features.system.language.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class LanguagePreferenceManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language_pref")
    }

    val languageFlow: Flow<AppLanguage> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { preferences ->
            val code = preferences[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.code
            AppLanguage.fromCode(code)
        }

    suspend fun saveLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
        }
    }
}