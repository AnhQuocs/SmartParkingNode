package com.example.smarttrafficradar.features.auth.domain.repository

import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<AuthUser?>
    fun getUserById(userId: String): Flow<AuthUser?>

    suspend fun deleteCurrentAccount()

    suspend fun signOut()

    // USER
    suspend fun signUp(username: String, email: String, password: String): AuthUser
    suspend fun signIn(email: String, password: String): AuthUser
    suspend fun changePassword(oldPassword: String, newPassword: String)

    // ADMIN
    suspend fun signUpAdmin(username: String, email: String, password: String, adminCode: String): AuthUser
}