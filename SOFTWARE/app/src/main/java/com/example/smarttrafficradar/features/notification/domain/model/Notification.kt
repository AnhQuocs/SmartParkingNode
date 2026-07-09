package com.example.smarttrafficradar.features.notification.domain.model

enum class BodyKey {
    BODY_GATE_IN,
    BODY_GATE_OUT,
    BODY_PARKING_OVER_30_MIN,
    BODY_PARKING_OVERNIGHT,
    BODY_PAYMENT_SUCCESS
}

enum class TitleKey {
    TITLE_GATE_IN,
    TITLE_GATE_OUT,
    TITLE_PARKING_OVER_30_MIN,
    TITLE_PARKING_OVERNIGHT,
    TITLE_PAYMENT_SUCCESS
}

data class Notification(
    val id: String = "",
    val userId: String = "",
    val titleKey: TitleKey? = null,
    val bodyKey: BodyKey? = null,
    val arguments: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)