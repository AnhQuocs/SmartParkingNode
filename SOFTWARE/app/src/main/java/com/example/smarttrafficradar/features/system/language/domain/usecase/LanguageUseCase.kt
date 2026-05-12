package com.example.smarttrafficradar.features.system.language.domain.usecase

import com.example.smarttrafficradar.features.system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.system.language.data.preference.LanguagePreferenceManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLanguageUseCase @Inject constructor(
    private val manager: LanguagePreferenceManager
) {
    operator fun invoke(): Flow<AppLanguage> = manager.languageFlow
}

class UpdateLanguageUseCase @Inject constructor(
    private val manager: LanguagePreferenceManager
) {
    suspend operator fun invoke(language: AppLanguage) = manager.saveLanguage(language)
}