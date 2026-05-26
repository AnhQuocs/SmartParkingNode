package com.example.smarttrafficradar.features.violation.data.repository

import com.example.smarttrafficradar.features.violation.data.dto.ViolationDto
import com.example.smarttrafficradar.features.violation.data.mapper.toDomain
import com.example.smarttrafficradar.features.violation.domain.model.Violation
import com.example.smarttrafficradar.features.violation.domain.repository.ViolationRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ViolationRepositoryImpl @Inject constructor(
    private val realtimeDb: FirebaseDatabase
) : ViolationRepository {

    override fun observeViolationList(nodeId: String): Flow<List<Violation>> = callbackFlow {
        val ref = realtimeDb.getReference("violations")
            .child(nodeId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { child ->
                        val dto = child.getValue(ViolationDto::class.java)
                        val id = child.key ?: return@mapNotNull null

                        dto?.toDomain(nodeId = nodeId, id = id)
                    }
                    .sortedByDescending { it.timestamp }

                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}