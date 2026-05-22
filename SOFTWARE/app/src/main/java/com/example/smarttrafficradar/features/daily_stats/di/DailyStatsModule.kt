<<<<<<< HEAD
package com.example.smarttrafficradar.features.daily_stats.di

import com.example.smarttrafficradar.features.daily_stats.data.repository.DailyStatsRepositoryImpl
import com.example.smarttrafficradar.features.daily_stats.domain.repository.DailyStatsRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DailyStatsModule {

    @Binds
    @Singleton
    abstract fun bindDailyStatsRepository(
        dailyStatsRepositoryImpl: DailyStatsRepositoryImpl
    ): DailyStatsRepository

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }
    }
=======
package com.example.smarttrafficradar.features.daily_stats.di

import com.example.smarttrafficradar.features.daily_stats.data.repository.DailyStatsRepositoryImpl
import com.example.smarttrafficradar.features.daily_stats.domain.repository.DailyStatsRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DailyStatsModule {

    @Binds
    @Singleton
    abstract fun bindDailyStatsRepository(
        dailyStatsRepositoryImpl: DailyStatsRepositoryImpl
    ): DailyStatsRepository

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseDatabase(): FirebaseDatabase {
            return FirebaseDatabase.getInstance()
        }
    }
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}