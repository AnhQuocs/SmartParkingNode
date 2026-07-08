package com.example.smarttrafficradar.features.app_system.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.auth.domain.usecase.UpdateFcmTokenUseCase
import com.example.smarttrafficradar.features.main.ui.MainActivity
import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.usecase.DeleteOldNotificationsUseCase
import com.example.smarttrafficradar.features.notification.domain.usecase.SaveNotificationUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    @Inject
    lateinit var saveNotificationUseCase: SaveNotificationUseCase

    @Inject
    lateinit var deleteOldNotificationsUseCase: DeleteOldNotificationsUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Firebase cấp Token mới ngầm: $token")

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (!currentUserId.isNullOrEmpty()) {
            serviceScope.launch {
                try {
                    updateFcmTokenUseCase(currentUserId, token)
                    Log.d("FCM_SERVICE", "Đã cập nhật token ngầm lên server thành công")
                } catch (e: Exception) {
                    Log.e("FCM_SERVICE", "Lỗi update token ngầm: ${e.message}")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FCM_SERVICE", "Nhận được thông báo mới từ server!")

        val title = message.notification?.title ?: message.data["title"] ?: "Thông báo hệ thống"
        val body = message.notification?.body ?: message.data["body"] ?: "Bạn có một thông báo mới."
        val type = message.data["type"] ?: "SYSTEM"
        
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (!currentUserId.isNullOrEmpty()) {
            serviceScope.launch {
                try {
                    // Lưu thông báo vào Firestore
                    saveNotificationUseCase(
                        Notification(
                            userId = currentUserId,
                            title = title,
                            body = body,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    // Dọn dẹp thông báo cũ hơn 3 tháng
                    deleteOldNotificationsUseCase()
                } catch (e: Exception) {
                    Log.e("FCM_SERVICE", "Lỗi lưu thông báo: ${e.message}")
                }
            }
        }

        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "smart_traffic_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("GO_TO_HISTORY", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Thông báo bãi đỗ xe", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
