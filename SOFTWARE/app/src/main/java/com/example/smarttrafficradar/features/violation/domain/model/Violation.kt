<<<<<<< HEAD
package com.example.smarttrafficradar.features.violation.domain.model

data class Violation(
    val nodeId: String,
    val id: String,
    val resolved: Boolean,
    val speedKmh: Double,
    val timestamp: Long,
    val vehicleId: String
=======
package com.example.smarttrafficradar.features.violation.domain.model

data class Violation(
    val nodeId: String,
    val id: String,
    val resolved: Boolean,
    val speedKmh: Double,
    val timestamp: Long,
    val vehicleId: String,
    val vMax: Int
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
)