package com.example.smarttrafficradar.features.management.data.dto

data class RegistrationRequestDto(
    var id: String? = null,
    var uid: String? = null,
    var fullName: String? = null,
    var identifier: String? = null,
    var status: String? = null,
    var timestamp: Long? = null,
    var vehicleType: String? = null
)
