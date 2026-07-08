package com.example.smarttrafficradar.features.notification.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("isRead")
    @set:PropertyName("isRead")
    var isRead: Boolean = false
)