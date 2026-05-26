package com.example.smarttrafficradar.features.system_monitor.di

import com.example.smarttrafficradar.features.system_monitor.data.repository.NetworkRepositoryImpl
import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SystemMonitorModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(
        impl: NetworkRepositoryImpl
    ): NetworkRepository
}
