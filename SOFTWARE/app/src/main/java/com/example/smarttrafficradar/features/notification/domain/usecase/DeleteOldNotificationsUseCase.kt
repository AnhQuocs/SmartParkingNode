package com.example.smarttrafficradar.features.notification.domain.usecase

import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteOldNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() {
        val threeMonthsInMillis = 3L * 30 * 24 * 60 * 60 * 1000
        val beforeTimestamp = System.currentTimeMillis() - threeMonthsInMillis
        repository.deleteOldNotifications(beforeTimestamp)
    }
}
