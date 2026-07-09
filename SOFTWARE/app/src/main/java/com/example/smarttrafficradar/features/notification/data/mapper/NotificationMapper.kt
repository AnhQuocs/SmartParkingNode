package com.example.smarttrafficradar.features.notification.data.mapper

import com.example.smarttrafficradar.features.notification.data.dto.NotificationDto
import com.example.smarttrafficradar.features.notification.domain.model.BodyKey
import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.model.TitleKey
import com.google.firebase.Timestamp
import java.util.Date

fun NotificationDto.toDomain(): Notification {
    return Notification(
        id = id,
        userId = userId,
        titleKey = try { TitleKey.valueOf(titleKey) } catch (e: Exception) { null },
        bodyKey = try { BodyKey.valueOf(bodyKey) } catch (e: Exception) { null },
        arguments = args,
        createdAt = createdAt.toDate().time,
        isRead = isRead
    )
}

fun Notification.toDto(): NotificationDto {
    return NotificationDto(
        id = id,
        userId = userId,
        titleKey = titleKey?.name ?: "",
        bodyKey = bodyKey?.name ?: "",
        args = arguments,
        createdAt = Timestamp(Date(createdAt)),
        isRead = isRead
    )
}