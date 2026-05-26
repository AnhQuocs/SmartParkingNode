package com.example.smarttrafficradar.features.violation.presentation.util

import android.content.Context
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.violation.presentation.ui.ViolationLevel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toRelativeTime(context: Context): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7

    return when {
        minutes < 1 -> context.getString(R.string.just_now)

        minutes < 60 -> context.resources.getQuantityString(
            R.plurals.minutes_ago,
            minutes.toInt(),
            minutes
        )

        hours < 24 -> context.resources.getQuantityString(
            R.plurals.hours_ago,
            hours.toInt(),
            hours
        )

        days < 7 -> context.resources.getQuantityString(
            R.plurals.days_ago,
            days.toInt(),
            days
        )

        days < 30 -> context.resources.getQuantityString(
            R.plurals.weeks_ago,
            weeks.toInt(),
            weeks
        )

        else -> {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            Instant.ofEpochMilli(this)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        }
    }
}

fun getViolationLevel(speed: Double, limit: Int): ViolationLevel {
    val excess = speed - limit

    return when {
        excess >= 20 -> ViolationLevel.CRITICAL
        excess >= 10 -> ViolationLevel.HIGH
        else -> ViolationLevel.MODERATE
    }
}