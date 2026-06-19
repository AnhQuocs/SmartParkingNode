package com.example.smarttrafficradar.features.dashboard.presentation.util

import java.text.NumberFormat
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

fun Int.toCurrencyFormat(): String {
    return NumberFormat.getNumberInstance(Locale("vi", "VN"))
        .format(this)
}