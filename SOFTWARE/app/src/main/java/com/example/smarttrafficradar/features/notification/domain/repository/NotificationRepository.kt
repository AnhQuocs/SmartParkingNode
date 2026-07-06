package com.example.smarttrafficradar.features.notification.domain.repository

import com.example.smarttrafficradar.features.notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun saveNotification(notification: Notification): Result<Unit>
    fun getNotifications(userId: String): Flow<List<Notification>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllAsRead(userId: String): Result<Unit>
    suspend fun deleteOldNotifications(beforeTimestamp: Long): Result<Unit>
}
