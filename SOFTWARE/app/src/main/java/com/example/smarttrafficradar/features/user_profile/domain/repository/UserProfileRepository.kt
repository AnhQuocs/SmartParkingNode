package com.example.smarttrafficradar.features.user_profile.domain.repository

import com.example.smarttrafficradar.features.user_profile.domain.model.UserLang
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getUserProfile(uid: String): Flow<UserProfile?>
    suspend fun saveUserProfile(userProfile: UserProfile)
    suspend fun updateDebt(uid: String, amount: Long)
    suspend fun updateLanguage(uid: String, language: UserLang)
    suspend fun checkIdentifierInOrganization(identifier: String, email: String, currentUid: String? = null): Boolean
    suspend fun isIdentifierTaken(identifier: String, currentUid: String): Boolean
    suspend fun getOrganizationMember(identifier: String, email: String): Map<String, Any>?
}
