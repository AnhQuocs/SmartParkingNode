package com.example.smarttrafficradar.features.system_config.di

import com.example.smarttrafficradar.features.system_config.data.repository.SystemConfigRepositoryImpl
import com.example.smarttrafficradar.features.system_config.domain.repository.SystemConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemConfigModule {

    @Binds
    @Singleton
    abstract fun bindSystemConfigRepository(
        impl: SystemConfigRepositoryImpl
    ): SystemConfigRepository
}
