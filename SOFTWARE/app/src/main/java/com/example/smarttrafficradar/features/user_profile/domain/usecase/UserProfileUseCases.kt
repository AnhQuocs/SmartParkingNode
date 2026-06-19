package com.example.smarttrafficradar.features.user_profile.domain.usecase

import javax.inject.Inject

class UserProfileUseCases @Inject constructor(
    val getUserProfile: GetUserProfileUseCase,
    val saveUserProfile: SaveUserProfileUseCase,
    val updateDebt: UpdateDebtUseCase,
    val checkIdentifierExists: CheckIdentifierExistsUseCase,
    val getOrganizationMember: GetOrganizationMemberUseCase
)
