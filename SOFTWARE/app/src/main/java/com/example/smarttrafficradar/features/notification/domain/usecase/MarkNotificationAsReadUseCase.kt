package com.example.smarttrafficradar.features.notification.domain.usecase

import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String) = repository.markAsRead(notificationId)
}
