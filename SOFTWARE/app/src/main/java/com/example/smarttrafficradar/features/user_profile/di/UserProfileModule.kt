package com.example.smarttrafficradar.features.user_profile.di

import com.example.smarttrafficradar.features.app_system.language.data.preference.LanguagePreferenceManager
import com.example.smarttrafficradar.features.user_profile.data.repository.UserProfileRepositoryImpl
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserProfileModule {

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        firestore: FirebaseFirestore,
        languagePreferenceManager: LanguagePreferenceManager
    ): UserProfileRepository =
        UserProfileRepositoryImpl(
            firestore,
            languagePreferenceManager
        )
}
