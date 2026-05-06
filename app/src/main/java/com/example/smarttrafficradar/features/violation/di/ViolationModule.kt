package com.example.smarttrafficradar.features.violation.di

import com.example.smarttrafficradar.features.violation.data.repository.ViolationRepositoryImpl
import com.example.smarttrafficradar.features.violation.domain.repository.ViolationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindViolationRepository(
        impl: ViolationRepositoryImpl
    ): ViolationRepository
}