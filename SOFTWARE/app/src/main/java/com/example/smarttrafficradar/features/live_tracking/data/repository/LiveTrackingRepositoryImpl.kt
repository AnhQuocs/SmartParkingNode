package com.example.smarttrafficradar.features.live_tracking.data.repository

import android.util.Log
import com.example.smarttrafficradar.features.live_tracking.data.dto.LiveTrackingDto
import com.example.smarttrafficradar.features.live_tracking.data.mapper.toDomain
import com.example.smarttrafficradar.features.live_tracking.domain.model.LastVehicle
import com.example.smarttrafficradar.features.live_tracking.domain.model.LiveTracking
import com.example.smarttrafficradar.features.live_tracking.domain.repository.LiveTrackingRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LiveTrackingRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) : LiveTrackingRepository {

    override fun observeLiveTracking(nodeId: String): Flow<LiveTracking> = callbackFlow {
        // SỬA Ở ĐÂY: Bỏ cái .child("latest_vehicle") đi
        val ref = realtimeDb.getReference("live_tracking")
            .child(nodeId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseData", "Snapshot: ${snapshot.value}")

                val dto = snapshot.getValue(LiveTrackingDto::class.java)
                Log.d("FirebaseData", "DTO: $dto")

                val liveTracking = dto?.toDomain(nodeId) ?: LiveTracking(
                    nodeId = nodeId,
                    lastVehicle = LastVehicle(false, 0.0, 0L, "No Data"),
                    recentEntries = emptyList()
                )

                trySend(liveTracking)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}