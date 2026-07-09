package com.example.smarttrafficradar.features.app_system.service

import android.util.Log
import com.example.smarttrafficradar.features.auth.domain.usecase.UpdateFcmTokenUseCase
import com.example.smarttrafficradar.features.notification.domain.usecase.DeleteOldNotificationsUseCase
import com.example.smarttrafficradar.features.notification.domain.usecase.SaveNotificationUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
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
}
