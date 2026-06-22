package com.example.smarttrafficradar.features.app_system.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

// Import UseCase của bro từ thư mục auth
import com.example.smarttrafficradar.features.auth.domain.usecase.UpdateFcmTokenUseCase
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.main.ui.MainActivity

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var updateFcmTokenUseCase: UpdateFcmTokenUseCase

    // Tạo một scope riêng cho Service để chạy Coroutine an toàn
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Firebase cấp Token mới ngầm: $token")

        // TODO: Lấy userId đang đăng nhập từ local (DataStore / SharedPreferences / Room)
        // Nếu bro có inject AuthRepository vào đây thì gọi ra lấy là đẹp nhất
        val currentUserId = "Fff4xFI4dUXTkEbTHctA4FtZewh2"

        if (currentUserId.isNotEmpty()) {
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

        // Lấy nội dung từ Payload. Firebase hỗ trợ 2 loại: Notification payload và Data payload.
        // Mình check cả 2 cho chắc ăn, ưu tiên Notification payload trước.
        val title = message.notification?.title ?: message.data["title"] ?: "Thông báo hệ thống"
        val body = message.notification?.body ?: message.data["body"] ?: "Bạn có một thông báo mới."

        showNotification(title, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "smart_traffic_notification_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Tạo Intent định nghĩa hành động mở MainActivity khi người dùng click
        val intent = Intent(this, MainActivity::class.java).apply {
            // Clear các activity cũ để đưa MainActivity lên vị trí cao nhất
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Có thể đính kèm thêm dữ liệu bổ sung để điều hướng sâu vào màn hình lịch sử
            putExtra("GO_TO_HISTORY", true)
        }

        // 2. Bọc Intent vào PendingIntent để hệ thống Android quản lý và thực thi từ bên ngoài app
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
            .setContentIntent(pendingIntent) // <--- THÊM DÒNG NÀY ĐỂ LIÊN KẾT HÀNH ĐỘNG CLICK
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}