package com.example.smarttrafficradar.features.auth.domain.repository

import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<AuthUser?>
    fun getUserById(userId: String): Flow<AuthUser?>

//    suspend fun updateSingleField(uid: String, fieldName: String, value: Any)
//    suspend fun updateUserFields(uid: String, updates: Map<String, Any>)

    suspend fun deleteCurrentAccount()

//    suspend fun reauthenticate(password: String): Result<Unit>
//    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
//
//    suspend fun signInWithGoogle(idToken: String): AuthUser
//    suspend fun reauthenticateWithGoogle(idToken: String): Result<Unit>

    suspend fun signOut()

    // USER
    suspend fun signUp(username: String, email: String, password: String): AuthUser
    suspend fun signIn(email: String, password: String): AuthUser

    // ADMIN
    suspend fun signUpAdmin(username: String, email: String, password: String, adminCode: String): AuthUser
}