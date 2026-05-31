package com.example.smarttrafficradar.features.system_monitor.data.repository

import com.example.smarttrafficradar.features.system_monitor.data.dto.SystemMonitorDto
import com.example.smarttrafficradar.features.system_monitor.data.dto.WifiConfigDto
import com.example.smarttrafficradar.features.system_monitor.data.mapper.toDomain
import com.example.smarttrafficradar.features.system_monitor.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiStatus
import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) : NetworkRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timeoutJob: Job? = null

    override fun getSystemMonitor(nodeId: String): Flow<SystemMonitor> = callbackFlow {
        val ref = realtimeDb.getReference("system_monitor").child(nodeId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(SystemMonitorDto::class.java)
                dto?.let { trySend(it.toDomain()) }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun updateWifiConfig(nodeId: String, ssid: String, password: String) {
        val ref = realtimeDb.getReference("network_setup").child(nodeId)
        val data = mapOf(
            "ssid" to ssid,
            "password" to password,
            "status" to "SWITCHING",
            "message" to "Connecting to new WiFi...",
            "timestamp" to System.currentTimeMillis()
        )
        ref.setValue(data).await()
        
        startTimeoutCheck(nodeId)
    }

    private fun startTimeoutCheck(nodeId: String) {
        timeoutJob?.cancel()
        timeoutJob = repositoryScope.launch {
            delay(20_000)
            val ref = realtimeDb.getReference("network_setup").child(nodeId)
            val snapshot = ref.get().await()
            val status = snapshot.child("status").value as? String
            if (status == "SWITCHING") {
                val updates = mapOf(
                    "status" to "FAILED",
                    "message" to "Connection Timeout. Device is not responding."
                )
                ref.updateChildren(updates)
            }
        }
    }

    override fun observeWifiConfig(nodeId: String): Flow<WifiConfig> = callbackFlow {
        val ref = realtimeDb.getReference("network_setup").child(nodeId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(WifiConfigDto::class.java)
                val config = dto?.toDomain() ?: WifiConfig(ssid = "")
                
                if (config.status == WifiStatus.SUCCESS || config.status == WifiStatus.FAILED) {
                    timeoutJob?.cancel()
                }
                
                trySend(config)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
