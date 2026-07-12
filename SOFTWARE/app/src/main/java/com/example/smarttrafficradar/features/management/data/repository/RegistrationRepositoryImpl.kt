package com.example.smarttrafficradar.features.management.data.repository

import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.data.mapper.toDomain
import com.example.smarttrafficradar.features.management.data.mapper.toDto
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase
) : RegistrationRepository {
    override suspend fun sendRegistrationRequest(request: RegistrationRequest) {
        db.getReference("registration_requests")
            .child(request.uid)
            .setValue(request.toDto())
            .await()
    }

    override fun getRegistrationRequests(): Flow<List<RegistrationRequest>> = callbackFlow {
        val ref = db.getReference("registration_requests")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = snapshot.children.mapNotNull { child ->
                    child.getValue(RegistrationRequestDto::class.java)?.toDomain()
                }
                trySend(requests)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override suspend fun updateRegistrationStatus(uid: String, status: RegistrationStatus) {
        db.getReference("registration_requests")
            .child(uid)
            .child("status")
            .setValue(status)
            .await()
    }
}