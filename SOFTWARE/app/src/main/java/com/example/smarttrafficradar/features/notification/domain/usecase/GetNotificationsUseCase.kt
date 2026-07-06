package com.example.smarttrafficradar.features.notification.domain.usecase

import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<List<Notification>> = repository.getNotifications(userId)
}
