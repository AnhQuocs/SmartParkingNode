package com.example.smarttrafficradar.features.violation.presentation.util

import com.example.smarttrafficradar.features.violation.presentation.ui.ViolationLevel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toFormattedDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}

fun getViolationLevel(speed: Double, limit: Int): ViolationLevel {
    val excess = speed - limit

    return when {
        excess >= 20 -> ViolationLevel.CRITICAL
        excess >= 10 -> ViolationLevel.HIGH
        else -> ViolationLevel.MODERATE
    }
}