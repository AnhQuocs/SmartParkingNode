package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfileError
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class CheckIdentifierExistsUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {

    suspend operator fun invoke(identifier: String, email: String, currentUid: String = ""): Result<Boolean> = try {
        val inOrg = repository.checkIdentifierInOrganization(identifier, email)
        if (!inOrg) {
            throw UserProfileError.IdentifierNotFound
        }

        val isTaken = repository.isIdentifierTaken(identifier, currentUid)
        if (isTaken) {
            throw UserProfileError.IdentifierAlreadyExists
        }

        Result.success(true)
    } catch (e: UserProfileError) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(UserProfileError.UnknownError(e.message ?: "Unknown error"))
    }
}
