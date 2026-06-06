package com.example.smarttrafficradar.features.auth.data.repository

import com.example.smarttrafficradar.features.auth.data.dto.AuthUserDto
import com.example.smarttrafficradar.features.auth.data.mapper.toDomain
import com.example.smarttrafficradar.features.auth.data.mapper.toDto
import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.model.UserRole
import com.example.smarttrafficradar.features.auth.domain.model.UserStatus
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val userCollection = firestore.collection("users")

    override fun getCurrentUser(): Flow<AuthUser?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val subscription = userCollection
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(AuthUserDto::class.java)?.toDomain()
                trySend(user)
            }

        awaitClose { subscription.remove() }
    }

    override fun getUserById(userId: String): Flow<AuthUser?> = callbackFlow {
        if (userId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val subscription = userCollection
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                if(snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(AuthUserDto::class.java)?.toDomain()
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun deleteCurrentAccount() {
        val currentUser = auth.currentUser ?: throw AuthError.UserNotFound()

        try {
            userCollection.document(currentUser.uid)
                .delete()
                .await()

            currentUser.delete().await()
        } catch (e: Exception) {
            throw mapFirebaseException(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    // USER
    override suspend fun signUp(username: String, email: String, password: String): AuthUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw AuthError.UnknownError()

            val user = AuthUser(
                uid = uid,
                email = email,
                username = username,
                avatar = null,
                role = UserRole.USER,
                status = UserStatus.PROFILE_INCOMPLETE,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            userCollection.document(uid)
                .set(user.toDto())
                .await()

            return user
        } catch (e: Exception) {
            try {
                auth.currentUser?.delete()?.await()
            } catch (_: Exception) {}
            throw mapFirebaseException(e)
        }
    }

    override suspend fun signIn(email: String, password: String): AuthUser {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw AuthError.UserNotFound()

            val snapshot = userCollection.document(uid).get().await()
            val userDto = snapshot.toObject(AuthUserDto::class.java)
                ?: throw AuthError.UserNotFound()
            return userDto.toDomain()
        } catch (e: Exception) {
            throw mapFirebaseException(e)
        }
    }

    // ADMIN
    override suspend fun signUpAdmin(
        username: String,
        email: String,
        password: String,
        adminCode: String
    ): AuthUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw AuthError.UnknownError()

            val codeDocRef = firestore.collection("codes").document(adminCode)
            val userRef = userCollection.document(uid)

            return firestore.runTransaction { transaction ->
                val snapshot = transaction.get(codeDocRef)

                if (!snapshot.exists()) {
                    throw AuthError.InvalidAdminCode()
                }

                val isUsed = snapshot.getBoolean("isUsed") ?: false
                if (isUsed) {
                    throw AuthError.InvalidAdminCode()
                }

                transaction.update(codeDocRef, mapOf(
                    "adminId" to uid,
                    "isUsed" to true
                ))

                val newUserDto = AuthUserDto(
                    uid = uid,
                    email = email,
                    username = username,
                    role = UserRole.ADMIN.name
                )

                transaction.set(userRef, newUserDto)
                newUserDto.toDomain()
            }.await()

        } catch (e: Exception) {
            try {
                auth.currentUser?.delete()?.await()
            } catch (_: Exception) {}
            
            if (e is AuthError) throw e
            throw mapFirebaseException(e)
        }
    }

    private fun mapFirebaseException(e: Exception): AuthError {
        return when (e) {
            is FirebaseAuthUserCollisionException -> AuthError.EmailAlreadyInUse()
            is FirebaseAuthInvalidUserException -> AuthError.UserNotFound()
            is FirebaseAuthInvalidCredentialsException -> AuthError.WrongPassword()
            else -> AuthError.RemoteError(e.message)
        }
    }
}