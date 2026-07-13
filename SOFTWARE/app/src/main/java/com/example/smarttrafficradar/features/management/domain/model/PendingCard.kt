package com.example.smarttrafficradar.features.management.domain.model

enum class PendingCardStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class PendingCard(
    val uid: String,
    val fullName: String,
    val identifier: String,
    val reason: String,
    val timestamp: Long,
    val status: PendingCardStatus
)
