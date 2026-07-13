package com.example.smarttrafficradar.features.management.data.dto

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PendingCardDto(
    val uid: String? = null,
    val fullName: String? = null,
    val identifier: String? = null,
    val reason: String? = null,
    val timestamp: Long? = null,
    val status: String? = null
)
