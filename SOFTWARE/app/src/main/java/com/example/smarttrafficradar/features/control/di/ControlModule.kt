package com.example.smarttrafficradar.features.control.di

import com.example.smarttrafficradar.features.control.data.repository.SystemMonitorRepositoryImpl
import com.example.smarttrafficradar.features.control.domain.repository.SystemMonitorRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ControlModule {

    @Provides
    @Singleton
    fun provideSystemMonitorRepository(
        database: FirebaseDatabase
    ): SystemMonitorRepository {
        return SystemMonitorRepositoryImpl(database)
    }
}
