package com.example.smarttrafficradar.features.system.language.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.smarttrafficradar.features.system.language.data.preference.LanguagePreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

    @Provides
    @Singleton
    fun provideLanguagePreferenceManager(
        dataStore: DataStore<Preferences>
    ): LanguagePreferenceManager = LanguagePreferenceManager(dataStore)
}