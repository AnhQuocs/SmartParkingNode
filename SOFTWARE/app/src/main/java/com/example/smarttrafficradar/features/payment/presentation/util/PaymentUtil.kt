package com.example.smarttrafficradar.features.payment.presentation.util

fun getInitials(fullName: String): String {
    val names = fullName.trim().split(" ").filter { it.isNotBlank() }
    return when {
        names.isEmpty() -> ""
        names.size == 1 -> names.first().take(1).uppercase()
        else -> (names.first().take(1) + names.last().take(1)).uppercase()
    }
}