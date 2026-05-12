package com.example.smarttrafficradar.features.system.appearance.di

import com.example.smarttrafficradar.features.system.appearance.data.repository.ThemeRepositoryImpl
import com.example.smarttrafficradar.features.system.appearance.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ThemeRepositoryModule {

    @Binds
    abstract fun bindThemeRepository(
        impl: ThemeRepositoryImpl
    ): ThemeRepository
}