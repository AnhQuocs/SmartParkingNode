package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class GetOrganizationMemberUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(identifier: String, email: String): Map<String, Any>? {
        return repository.getOrganizationMember(identifier, email)
    }
}
