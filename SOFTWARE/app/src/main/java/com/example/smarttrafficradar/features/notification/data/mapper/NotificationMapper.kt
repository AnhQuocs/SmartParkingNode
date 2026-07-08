package com.example.smarttrafficradar.features.notification.data.mapper

import com.example.smarttrafficradar.features.notification.data.dto.NotificationDto
import com.example.smarttrafficradar.features.notification.domain.model.Notification

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = id,
        userId = userId,
        title = title,
        body = body,
        timestamp = createdAt.toDate().time,
        isRead = isRead
    )
}