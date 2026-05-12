package com.example.smarttrafficradar.features.daily_stats.data.repository

import com.example.smarttrafficradar.features.daily_stats.data.dto.DailyStatsDto
import com.example.smarttrafficradar.features.daily_stats.data.mapper.toDomain
import com.example.smarttrafficradar.features.daily_stats.domain.model.DailyStats
import com.example.smarttrafficradar.features.daily_stats.domain.repository.DailyStatsRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

data class DailyStatsRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) : DailyStatsRepository {

    override fun getDailyStatsFlow(nodeId: String, date: String): Flow<DailyStats> = callbackFlow {
        val ref = realtimeDb.getReference("daily_stats")
            .child(nodeId)
            .child(date)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(DailyStatsDto::class.java)

                val dailyStats =
                    dto?.toDomain(nodeId, date) ?: DailyStats(nodeId, date, 0.0, "N/A", 0, 0)

                trySend(dailyStats)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}