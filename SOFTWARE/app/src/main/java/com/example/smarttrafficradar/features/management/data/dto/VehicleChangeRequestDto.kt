package com.example.smarttrafficradar.features.management.data.dto

data class VehicleChangeRequestDto(
    var uid: String? = null,
    var fullName: String? = null,
    var identifier: String? = null,
    val rfidUid: String? = null,
    var currentVehicleType: String? = null,
    var requestedVehicleType: String? = null,
    var memberType: String? = null,
    var timestamp: Long? = null,
    var status: String? = null
)
