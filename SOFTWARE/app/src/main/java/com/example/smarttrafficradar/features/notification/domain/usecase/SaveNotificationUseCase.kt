package com.example.smarttrafficradar.features.notification.domain.usecase

import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification) = repository.saveNotification(notification)
}
