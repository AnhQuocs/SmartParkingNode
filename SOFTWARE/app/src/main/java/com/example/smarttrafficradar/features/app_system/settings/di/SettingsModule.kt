package com.example.smarttrafficradar.features.app_system.settings.di

import com.example.smarttrafficradar.features.app_system.settings.data.repository.SettingsRepositoryImpl
import com.example.smarttrafficradar.features.app_system.settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository
}
