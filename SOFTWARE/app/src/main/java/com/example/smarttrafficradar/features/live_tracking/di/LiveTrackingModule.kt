package com.example.smarttrafficradar.features.live_tracking.di

import com.example.smarttrafficradar.features.live_tracking.data.repository.LiveTrackingRepositoryImpl
import com.example.smarttrafficradar.features.live_tracking.domain.repository.LiveTrackingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LiveTrackingModule {

    @Binds
    @Singleton
    abstract fun bindLiveTrackingRepository(
        liveTrackingRepositoryImpl: LiveTrackingRepositoryImpl
    ): LiveTrackingRepository
}