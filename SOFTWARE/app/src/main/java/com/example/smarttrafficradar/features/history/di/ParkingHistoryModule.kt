package com.example.smarttrafficradar.features.history.di

import com.example.smarttrafficradar.features.history.data.repository.ParkingHistoryRepositoryImpl
import com.example.smarttrafficradar.features.history.domain.repository.ParkingHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ParkingHistoryModule {

    @Binds
    @Singleton
    abstract fun bindParkingHistoryRepository(
        impl: ParkingHistoryRepositoryImpl
    ): ParkingHistoryRepository
}