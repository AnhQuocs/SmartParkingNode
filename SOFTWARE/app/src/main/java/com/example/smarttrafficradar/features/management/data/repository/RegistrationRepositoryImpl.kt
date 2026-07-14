package com.example.smarttrafficradar.features.management.data.repository

import com.example.smarttrafficradar.features.management.data.dto.RegisteredCardDto
import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.data.mapper.toDomain
import com.example.smarttrafficradar.features.management.data.mapper.toDto
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.repository.RegistrationRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    db: FirebaseDatabase,
    firestore: FirebaseFirestore
) : RegistrationRepository {

    private val registrationRef = db.getReference("registration_requests")
    private val cardsCollection = firestore.collection("registered_cards")

    override suspend fun sendRegistrationRequest(request: RegistrationRequest) {
        registrationRef.child(request.uid).setValue(request.toDto()).await()
    }

    override fun getRegistrationRequests(): Flow<List<RegistrationRequest>> = callbackFlow {
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
        registrationRef.addValueEventListener(listener)
        awaitClose { registrationRef.removeEventListener(listener) }
    }

    override suspend fun handleRegistrationDecision(uid: String, status: RegistrationStatus) {
        if (status == RegistrationStatus.APPROVED) {
            val snapshot = registrationRef.child(uid).get().await()
            val request = snapshot.getValue(RegistrationRequestDto::class.java)?.toDomain()

            request?.let {
                val now = System.currentTimeMillis()

                val card = RegisteredCard(
                    id = it.identifier,
                    userId = it.uid,
                    rfidUid = it.rfidUid,
                    ownerName = it.fullName,
                    identifier = it.identifier,
                    department = it.department,
                    cardType = it.cardType,
                    vehicleType = it.vehicleType,
                    status = CardStatus.ACTIVE,
                    registeredAt = now,
                    lastUsedAt = now
                )
                cardsCollection.document(card.id).set(card.toDto()).await()
            }
        }
        // Sau khi duyệt hoặc từ chối, xóa yêu cầu khỏi Realtime Database
        registrationRef.child(uid).removeValue().await()
    }

    override fun getRegisteredCards(): Flow<List<RegisteredCard>> {
        return cardsCollection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val dto = doc.toObject(RegisteredCardDto::class.java)
                    dto?.copy(id = doc.id)?.toDomain()
                } catch (e: Exception) {
                    android.util.Log.e("FirestoreError", "Document [${doc.id}] bị sai định dạng: ${e.message}")
                    null
                }
            }
        }
    }

    override suspend fun updateCardStatus(cardId: String, status: CardStatus) {
        cardsCollection.document(cardId).update("status", status.name).await()
    }

    override suspend fun updateRegistrationStatus(uid: String, status: RegistrationStatus) {
        handleRegistrationDecision(uid, status)
    }
}
