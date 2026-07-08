package com.example.smarttrafficradar.features.user_profile.domain.usecase

import com.example.smarttrafficradar.features.user_profile.domain.model.UserLang
import com.example.smarttrafficradar.features.user_profile.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileLanguageUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    suspend operator fun invoke(uid: String, language: UserLang) {
        repository.updateLanguage(uid, language)
    }
}
