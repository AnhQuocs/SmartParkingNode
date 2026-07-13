package com.example.smarttrafficradar.features.management.domain.repository

import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import kotlinx.coroutines.flow.Flow

interface OrganizationMemberRepository {
    fun getOrganizationMembers(): Flow<List<OrganizationMember>>
}
