package com.example.smarttrafficradar.features.dashboard.presentation.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.toDateString(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        .format(Date(this))
}

fun Long.toTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date(this))
}