package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfileError
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Unit> = try {
        // 1. Validate Full Name
        if (userProfile.fullName.isBlank() || userProfile.fullName.length < 2) {
            throw UserProfileError.InvalidFullName
        }

        // 2. Check if identifier exists in Organization list (Matched with Email)
        val existsInOrg = repository.checkIdentifierInOrganization(userProfile.identifier, userProfile.email)
        if (!existsInOrg) {
            throw UserProfileError.IdentifierNotFound
        }

        // 3. Check if identifier is already taken by another UID
        val isTaken = repository.isIdentifierTaken(userProfile.identifier, userProfile.uid)
        if (isTaken) {
            throw UserProfileError.IdentifierAlreadyExists
        }

        repository.saveUserProfile(userProfile)
        Result.success(Unit)
    } catch (e: UserProfileError) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(UserProfileError.UnknownError(e.message ?: "Unknown error"))
    }
}
