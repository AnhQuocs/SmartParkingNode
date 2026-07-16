package com.example.smarttrafficradar.features.dashboard.di

import com.example.smarttrafficradar.features.dashboard.data.repository.AnalyticsRepositoryImpl
import com.example.smarttrafficradar.features.dashboard.domain.repository.AnalyticsRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        database: FirebaseDatabase
    ): AnalyticsRepository {
        return AnalyticsRepositoryImpl(database)
    }
}
