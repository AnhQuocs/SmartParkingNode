package com.example.smarttrafficradar.features.notification.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class NotificationDto(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val titleKey: String = "",
    val bodyKey: String = "",
    val args: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false
)