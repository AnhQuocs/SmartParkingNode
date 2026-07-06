package com.example.smarttrafficradar.features.notification.domain.usecase

import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(userId: String) = repository.markAllAsRead(userId)
}
