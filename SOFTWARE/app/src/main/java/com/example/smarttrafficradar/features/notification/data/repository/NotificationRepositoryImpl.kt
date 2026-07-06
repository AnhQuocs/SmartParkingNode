package com.example.smarttrafficradar.features.notification.data.repository

import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    private val notificationCollection = firestore.collection("notifications")

    override fun getNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val subscription = notificationCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshot?.toObjects(Notification::class.java) ?: emptyList()
                trySend(notifications)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun saveNotification(notification: Notification): Result<Unit> = try {
        val docRef = notificationCollection.document()
        val notificationWithId = notification.copy(id = docRef.id)
        docRef.set(notificationWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = try {
        notificationCollection.document(notificationId).update("isRead", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markAllAsRead(userId: String): Result<Unit> = try {
        val unreadNotifications = notificationCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        if (!unreadNotifications.isEmpty) {
            val batch = firestore.batch()
            for (doc in unreadNotifications.documents) {
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteOldNotifications(beforeTimestamp: Long): Result<Unit> = try {
        val oldNotifications = notificationCollection
            .whereLessThan("timestamp", beforeTimestamp)
            .get()
            .await()

        if (!oldNotifications.isEmpty) {
            val batch = firestore.batch()
            for (doc in oldNotifications.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
