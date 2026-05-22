<<<<<<< HEAD
package com.example.smarttrafficradar.features.daily_stats.data.mapper

import com.example.smarttrafficradar.features.daily_stats.data.dto.DailyStatsDto
import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats

fun DailyStatsDto.toDomain(nodeId: String, date: String): DailyStats {
    return DailyStats(
        nodeId = nodeId,
        date = date,
        averageSpeed = averageSpeed ?: 0.0,
        peakHour = peakHour ?: "N/A",
        totalVehicles = totalVehicles ?: 0,
        totalViolations = totalViolations ?: 0
    )
=======
package com.example.smarttrafficradar.features.daily_stats.data.mapper

import com.example.smarttrafficradar.features.daily_stats.data.dto.DailyStatsDto
import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats

fun DailyStatsDto.toDomain(nodeId: String, date: String): DailyStats {
    return DailyStats(
        nodeId = nodeId,
        date = date,
        averageSpeed = averageSpeed ?: 0.0,
        peakHour = peakHour ?: "N/A",
        totalVehicles = totalVehicles ?: 0,
        totalViolations = totalViolations ?: 0
    )
>>>>>>> 6df0a61190a991344ecbb663b8b622d7e571a78a
}