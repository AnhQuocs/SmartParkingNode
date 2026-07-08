package com.example.smarttrafficradar.features.user_profile.data.repository

import com.example.smarttrafficradar.features.app_system.language.data.preference.LanguagePreferenceManager
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.user_profile.data.dto.UserProfileDto
import com.example.smarttrafficradar.features.user_profile.data.mapper.toDomain
import com.example.smarttrafficradar.features.user_profile.data.mapper.toDto
import com.example.smarttrafficradar.features.user_profile.domain.model.UserLang
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val languagePreferenceManager: LanguagePreferenceManager
) : UserProfileRepository {

    private val profilesCollection = firestore.collection("profiles")
    private val authUsersCollection = firestore.collection("users")
    private val orgUsersCollection = firestore.collection("organization_members")

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val profileFlowCache = ConcurrentHashMap<String, Flow<UserProfile?>>()

    override fun getUserProfile(uid: String): Flow<UserProfile?> {
        if (uid.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(null)
        }

        return profileFlowCache.getOrPut(uid) {
            callbackFlow {
                val subscription = profilesCollection
                    .document(uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val profile = snapshot?.toObject(UserProfileDto::class.java)?.toDomain()
                        trySend(profile)
                    }

                awaitClose { subscription.remove() }
            }.shareIn(
                scope = repositoryScope,
                started = SharingStarted.WhileSubscribed(5000L),
                replay = 1
            )
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        // Read current app language from DataStore
        val currentAppLang = languagePreferenceManager.languageFlow.first()
        val userLang = when (currentAppLang) {
            AppLanguage.ENGLISH -> UserLang.EN
            AppLanguage.VIETNAMESE -> UserLang.VI
        }

        // Attach current language to the profile
        val profileToSave = userProfile.copy(language = userLang)

        // Find the organization member document first to update linkedUid
        val orgQuery = orgUsersCollection
            .whereEqualTo("identifier", profileToSave.identifier)
            .whereEqualTo("email", profileToSave.email)
            .limit(1)
            .get()
            .await()

        firestore.runBatch { batch ->
            // 1. Save profile data to 'profiles' collection
            val profileRef = profilesCollection.document(profileToSave.uid)
            batch.set(profileRef, profileToSave.toDto())
            
            // 2. Update status to ACTIVE in 'users' collection (AuthUser)
            val authUserRef = authUsersCollection.document(profileToSave.uid)
            batch.update(authUserRef, "status", "ACTIVE")

            // 3. Update linkedUid in 'organization_members'
            if (!orgQuery.isEmpty) {
                batch.update(orgQuery.documents.first().reference, "linkedUid", profileToSave.uid)
            }
        }.await()
    }

    override suspend fun updateDebt(uid: String, amount: Long) {
        val profileRef = profilesCollection.document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(profileRef)
            val currentDebt = snapshot.getLong("currentDebt") ?: 0L
            transaction.update(profileRef, "currentDebt", currentDebt + amount)
            transaction.update(profileRef, "updatedAt", System.currentTimeMillis())
        }.await()
    }

    override suspend fun updateLanguage(uid: String, language: UserLang) {
        profilesCollection.document(uid).update(
            "language", language.name,
            "updatedAt", System.currentTimeMillis()
        ).await()
    }

    override suspend fun checkIdentifierInOrganization(identifier: String, email: String): Boolean {
        val query = orgUsersCollection
            .whereEqualTo("identifier", identifier)
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        if (query.isEmpty) return false

        val linkedUid = query.documents.first().getString("linkedUid")
        // Return true if it matches and is not linked to any account yet
        return linkedUid == null
    }

    override suspend fun isIdentifierTaken(identifier: String, currentUid: String): Boolean {
        val query = profilesCollection.whereEqualTo("identifier", identifier).limit(1).get().await()
        if (query.isEmpty) return false
        val foundUid = query.documents.first().id
        return foundUid != currentUid
    }

    override suspend fun getOrganizationMember(identifier: String, email: String): Map<String, Any>? {
        val query = orgUsersCollection
            .whereEqualTo("identifier", identifier)
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()
        
        return if (!query.isEmpty) query.documents.first().data else null
    }
}
