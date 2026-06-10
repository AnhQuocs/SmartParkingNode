package com.example.smarttrafficradar.features.user_profile.data.repository

import com.example.smarttrafficradar.features.user_profile.data.dto.UserProfileDto
import com.example.smarttrafficradar.features.user_profile.data.mapper.toDomain
import com.example.smarttrafficradar.features.user_profile.data.mapper.toDto
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserProfileRepository {

    private val profilesCollection = firestore.collection("profiles")
    private val authUsersCollection = firestore.collection("users")
    private val orgUsersCollection = firestore.collection("organization_members")

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val subscription = profilesCollection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val profile = snapshot?.toObject(UserProfileDto::class.java)?.toDomain()
            trySend(profile)
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        // Find the organization member document first to update linkedUid
        val orgQuery = orgUsersCollection
            .whereEqualTo("identifier", userProfile.identifier)
            .whereEqualTo("email", userProfile.email)
            .limit(1)
            .get()
            .await()

        firestore.runBatch { batch ->
            // 1. Save profile data to 'profiles' collection
            val profileRef = profilesCollection.document(userProfile.uid)
            batch.set(profileRef, userProfile.toDto())
            
            // 2. Update status to ACTIVE in 'users' collection (AuthUser)
            val authUserRef = authUsersCollection.document(userProfile.uid)
            batch.update(authUserRef, "status", "ACTIVE")

            // 3. Update linkedUid in 'organization_members'
            if (!orgQuery.isEmpty) {
                batch.update(orgQuery.documents.first().reference, "linkedUid", userProfile.uid)
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
