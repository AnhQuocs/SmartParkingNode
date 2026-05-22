<<<<<<< HEAD
package com.example.smarttrafficradar.features.live_tracking.domain.model

data class LiveTracking(
    val nodeId: String,
    val lastVehicle: LastVehicle,
    val recentEntries: List<SpeedEntry>
)

data class LastVehicle(
    val isViolation: Boolean,
    val speedKmh: Double,
    val timestamp: Long,
    val vehicleId: String
)

data class SpeedEntry(
    val speed: Double,
    val isViolation: Boolean
=======
package com.example.smarttrafficradar.features.live_tracking.domain.model

data class LiveTracking(
    val nodeId: String,
    val lastVehicle: LastVehicle,
    val recentEntries: List<SpeedEntry>
)

data class LastVehicle(
    val isViolation: Boolean,
    val speedKmh: Double,
    val timestamp: Long,
    val vehicleId: String
)

data class SpeedEntry(
    val speed: Double,
    val isViolation: Boolean
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
)