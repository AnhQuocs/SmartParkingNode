package com.example.smarttrafficradar.features.system_config.data.repository

import com.example.smarttrafficradar.features.system_config.data.dto.SystemConfigDto
import com.example.smarttrafficradar.features.system_config.data.mapper.toDomain
import com.example.smarttrafficradar.features.system_config.domain.model.SystemConfig
import com.example.smarttrafficradar.features.system_config.domain.repository.SystemConfigRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SystemConfigRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) : SystemConfigRepository {

    override fun getSystemConfig(nodeId: String): Flow<SystemConfig> = callbackFlow {
        val ref = realtimeDb.getReference("system_config")
            .child(nodeId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(SystemConfigDto::class.java)
                val config = dto?.toDomain(nodeId) ?: SystemConfig(
                    nodeId = nodeId,
                    laserEnabled = false,
                    lastPing = 0,
                    status = "offline",
                    vMaxThreshold = 0,
                    isOnline = false
                )
                trySend(config)
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
