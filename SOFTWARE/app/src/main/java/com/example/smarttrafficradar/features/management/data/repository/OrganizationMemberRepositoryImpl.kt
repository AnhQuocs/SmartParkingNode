package com.example.smarttrafficradar.features.management.data.repository

import com.example.smarttrafficradar.features.management.data.dto.OrganizationMemberDto
import com.example.smarttrafficradar.features.management.data.mapper.toDomain
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.domain.repository.OrganizationMemberRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrganizationMemberRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrganizationMemberRepository {

    private val membersCollection = firestore.collection("organization_members")

    override fun getOrganizationMembers(): Flow<List<OrganizationMember>> {
        return membersCollection.snapshots().map { snapshot ->
            snapshot.toObjects(OrganizationMemberDto::class.java).map { it.toDomain() }
        }
    }
}
