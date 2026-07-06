package com.example.smarttrafficradar.features.notification.presentation.ui

import com.example.smarttrafficradar.features.notification.domain.model.Notification

data class NotificationState(
    val notifications: List<Notification> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
