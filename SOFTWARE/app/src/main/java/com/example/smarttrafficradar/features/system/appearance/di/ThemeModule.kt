<<<<<<< HEAD
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
=======
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
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}