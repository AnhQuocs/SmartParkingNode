package com.example.smarttrafficradar.features.system_monitor.data.repository

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import com.example.smarttrafficradar.features.system_monitor.data.dto.WifiConfigDto
import com.example.smarttrafficradar.features.system_monitor.data.mapper.toDomain
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiConfig
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiNetwork
import com.example.smarttrafficradar.features.system_monitor.domain.model.WifiStatus
import com.example.smarttrafficradar.features.system_monitor.domain.repository.NetworkRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val realtimeDb: FirebaseDatabase
) : NetworkRepository {

    private val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var timeoutJob: Job? = null

    @SuppressLint("MissingPermission")
    override fun scanWifiNetworks(): Flow<List<WifiNetwork>> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val results = wifiManager.scanResults
                    val networks = results.map { result ->
                        WifiNetwork(
                            ssid = result.SSID,
                            signalLevel = WifiManager.calculateSignalLevel(result.level, 5),
                            isSecure = result.capabilities.contains("WPA") || result.capabilities.contains("WEP")
                        )
                    }.filter { it.ssid.isNotEmpty() }.distinctBy { it.ssid }
                    trySend(networks)
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {}
        }
    }

    override suspend fun updateWifiConfig(nodeId: String, ssid: String, password: String) {
        val ref = realtimeDb.getReference("network_setup").child(nodeId)
        val data = mapOf(
            "ssid" to ssid,
            "password" to password,
            "status" to "PENDING",
            "message" to "Waiting for device...",
            "timestamp" to System.currentTimeMillis()
        )
        ref.setValue(data).await()
        
        // Start timeout management at repository level
        startTimeoutCheck(nodeId)
    }

    private fun startTimeoutCheck(nodeId: String) {
        timeoutJob?.cancel()
        timeoutJob = repositoryScope.launch {
            delay(20_000)
            val ref = realtimeDb.getReference("network_setup").child(nodeId)
            val snapshot = ref.get().await()
            val status = snapshot.child("status").value as? String
            if (status == "PENDING") {
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
                
                // If device responded or timeout triggered, cancel the local timeout timer
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
