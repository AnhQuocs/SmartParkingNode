<<<<<<< HEAD
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
=======
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
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}