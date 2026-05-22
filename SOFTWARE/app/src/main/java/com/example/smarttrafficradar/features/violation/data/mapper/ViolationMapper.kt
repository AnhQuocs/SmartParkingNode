<<<<<<< HEAD
package com.example.smarttrafficradar.features.violation.data.mapper

import com.example.smarttrafficradar.features.violation.data.dto.ViolationDto
import com.example.smarttrafficradar.features.violation.domain.model.Violation

fun ViolationDto.toDomain(
    nodeId: String,
    id: String
): Violation {
    return Violation(
        nodeId = nodeId,
        id = id,
        resolved = resolved ?: false,
        speedKmh = speedKmh ?: 0.0,
        timestamp = timestamp ?: 0L,
        vehicleId = vehicleId ?: ""
    )
=======
package com.example.smarttrafficradar.features.violation.data.mapper

import com.example.smarttrafficradar.features.violation.data.dto.ViolationDto
import com.example.smarttrafficradar.features.violation.domain.model.Violation

fun ViolationDto.toDomain(
    nodeId: String,
    id: String
): Violation {
    return Violation(
        nodeId = nodeId,
        id = id,
        resolved = resolved ?: false,
        speedKmh = speedKmh ?: 0.0,
        timestamp = timestamp ?: 0L,
        vehicleId = vehicleId ?: "",
        vMax = vMax ?: 0
    )
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}