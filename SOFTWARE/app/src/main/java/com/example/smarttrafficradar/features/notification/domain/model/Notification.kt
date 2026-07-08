package com.example.smarttrafficradar.features.notification.domain.model

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)