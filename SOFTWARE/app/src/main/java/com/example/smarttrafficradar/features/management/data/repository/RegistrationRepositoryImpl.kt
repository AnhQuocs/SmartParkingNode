package com.example.smarttrafficradar.features.management.data.repository

import com.example.smarttrafficradar.features.management.data.dto.RegisteredCardDto
import com.example.smarttrafficradar.features.management.data.dto.RegistrationRequestDto
import com.example.smarttrafficradar.features.management.data.dto.VehicleChangeRequestDto
import com.example.smarttrafficradar.features.management.data.mapper.toDomain
import com.example.smarttrafficradar.features.management.data.mapper.toDto
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.model.RegistrationStatus
import com.example.smarttrafficradar.features.management.domain.model.VehicleChangeRequest
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
    private val firestore: FirebaseFirestore
) : RegistrationRepository {

    private val registrationRef = db.getReference("registration_requests")
    private val vehicleChangeRef = db.getReference("vehicle_change_requests")
    private val cardsCollection = firestore.collection("registered_cards")
    private val profilesCollection = firestore.collection("profiles")

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
            val requestDto = snapshot.getValue(RegistrationRequestDto::class.java)
            val request = requestDto?.toDomain()

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

                profilesCollection.document(it.uid).update(
                    mapOf(
                        "isActive" to true,
                        "rfidUid" to it.rfidUid,
                        "vehicleType" to it.vehicleType.name
                    )
                ).await()
            }
        }
        registrationRef.child(uid).removeValue().await()
    }

    override fun getRegisteredCards(): Flow<List<RegisteredCard>> {
        return cardsCollection.snapshots().map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                try {
                    val dto = doc.toObject(RegisteredCardDto::class.java)
                    dto?.copy(id = doc.id)?.toDomain()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun updateCardStatus(cardId: String, status: CardStatus) {
        cardsCollection.document(cardId).update("status", status.name).await()
    }

    override suspend fun lockCard(uid: String, cardId: String) {
        cardsCollection.document(cardId).update("status", CardStatus.BLOCKED.name).await()
        profilesCollection.document(uid).update("isActive", false).await()
    }

    override suspend fun sendVehicleChangeRequest(request: VehicleChangeRequest) {
        vehicleChangeRef.child(request.uid).setValue(request.toDto()).await()
    }

    override fun getVehicleChangeRequests(): Flow<List<VehicleChangeRequest>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = snapshot.children.mapNotNull { child ->
                    child.getValue(VehicleChangeRequestDto::class.java)?.toDomain()
                }
                trySend(requests)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        vehicleChangeRef.addValueEventListener(listener)
        awaitClose { vehicleChangeRef.removeEventListener(listener) }
    }

    override suspend fun handleVehicleChangeDecision(uid: String, status: RegistrationStatus) {
        if (status == RegistrationStatus.APPROVED) {
            val snapshot = vehicleChangeRef.child(uid).get().await()
            val requestDto = snapshot.getValue(VehicleChangeRequestDto::class.java)
            val request = requestDto?.toDomain()

            request?.let {
                // Update registered_cards
                val cardsQuery = cardsCollection.whereEqualTo("userId", it.uid).get().await()
                for (doc in cardsQuery.documents) {
                    doc.reference.update("vehicleType", it.requestedVehicleType.name).await()
                }

                // Update profiles
                profilesCollection.document(it.uid).update("vehicleType", it.requestedVehicleType.name).await()
            }
        }
        vehicleChangeRef.child(uid).removeValue().await()
    }

    override suspend fun updateRegistrationStatus(uid: String, status: RegistrationStatus) {
        handleRegistrationDecision(uid, status)
    }
}
