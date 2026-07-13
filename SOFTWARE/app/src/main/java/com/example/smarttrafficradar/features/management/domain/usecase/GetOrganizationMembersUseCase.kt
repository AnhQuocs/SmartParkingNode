package com.example.smarttrafficradar.features.management.domain.usecase

import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.domain.repository.OrganizationMemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOrganizationMembersUseCase @Inject constructor(
    private val repository: OrganizationMemberRepository
) {
    operator fun invoke(): Flow<List<OrganizationMember>> {
        return repository.getOrganizationMembers()
    }
}
