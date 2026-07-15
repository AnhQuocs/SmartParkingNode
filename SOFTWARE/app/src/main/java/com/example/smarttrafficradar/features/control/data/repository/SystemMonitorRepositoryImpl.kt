package com.example.smarttrafficradar.features.control.data.repository

import android.util.Log
import com.example.smarttrafficradar.features.control.data.dto.SystemMonitorDto
import com.example.smarttrafficradar.features.control.data.mapper.toDomain
import com.example.smarttrafficradar.features.control.domain.model.SystemMonitor
import com.example.smarttrafficradar.features.control.domain.repository.SystemMonitorRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SystemMonitorRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : SystemMonitorRepository {

    override fun getSystemMonitor(): Flow<SystemMonitor> = callbackFlow {
        val ref = database.getReference("system_monitor")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(SystemMonitorDto::class.java)
                dto?.let {
                    trySend(it.toDomain())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log lỗi thay vì close(error) gây crash khi logout
                Log.e("SystemMonitorRepo", "Database error: ${error.message}")
            }
        }

        ref.addValueEventListener(listener)
        
        awaitClose {
            ref.removeEventListener(listener)
        }
    }
}
