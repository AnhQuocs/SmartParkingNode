package com.example.smarttrafficradar.features.management.di

import com.example.smarttrafficradar.features.management.data.repository.OrganizationMemberRepositoryImpl
import com.example.smarttrafficradar.features.management.data.repository.PendingCardRepositoryImpl
import com.example.smarttrafficradar.features.management.data.repository.RegistrationRepositoryImpl
import com.example.smarttrafficradar.features.management.domain.repository.OrganizationMemberRepository
import com.example.smarttrafficradar.features.management.domain.repository.PendingCardRepository
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagementModule {

    @Provides
    @Singleton
    fun provideRegistrationRepository(
        db: FirebaseDatabase,
        firestore: FirebaseFirestore
    ): RegistrationRepository = RegistrationRepositoryImpl(db, firestore)

    @Provides
    @Singleton
    fun provideOrganizationMemberRepository(
        firestore: FirebaseFirestore
    ): OrganizationMemberRepository = OrganizationMemberRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providePendingCardRepository(
        db: FirebaseDatabase
    ): PendingCardRepository = PendingCardRepositoryImpl(db)
}