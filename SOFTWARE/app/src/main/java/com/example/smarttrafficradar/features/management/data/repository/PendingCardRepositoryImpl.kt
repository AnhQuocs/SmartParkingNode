package com.example.smarttrafficradar.features.management.data.repository

import com.example.smarttrafficradar.features.management.data.dto.PendingCardDto
import com.example.smarttrafficradar.features.management.data.mapper.toDomain
import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.domain.model.PendingCardStatus
import com.example.smarttrafficradar.features.management.domain.repository.PendingCardRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PendingCardRepositoryImpl @Inject constructor(
    db: FirebaseDatabase
) : PendingCardRepository {

    private val pendingCardsRef = db.getReference("pending_cards")

    override fun getPendingCards(): Flow<List<PendingCard>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    child.getValue(PendingCardDto::class.java)?.toDomain()
                }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        pendingCardsRef.addValueEventListener(listener)
        awaitClose { pendingCardsRef.removeEventListener(listener) }
    }

    override suspend fun updatePendingCardStatus(uid: String, status: PendingCardStatus) {
        pendingCardsRef.child(uid).child("status").setValue(status.name).await()
    }
}
