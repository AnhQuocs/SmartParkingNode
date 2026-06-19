package com.example.smarttrafficradar.features.history.domain.model

sealed class ParkingHistoryError(message: String? = null) : Exception(message) {

    object HistoryNotFound : ParkingHistoryError(
        "Parking history not found"
    )

    object InvalidHistoryData : ParkingHistoryError(
        "Parking history data is invalid"
    )

    object InvalidHistoryId : ParkingHistoryError(
        "History id is invalid"
    )

    object InvalidUserId : ParkingHistoryError(
        "User id is invalid"
    )

    object PermissionDenied : ParkingHistoryError(
        "Permission denied"
    )

    object NetworkError : ParkingHistoryError(
        "Network connection error"
    )

    data class UnknownError(
        val msg: String
    ) : ParkingHistoryError(msg)
}