package com.example.smarttrafficradar.features.system.appearance.domain.usecase

import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import com.example.smarttrafficradar.features.system.appearance.domain.repository.ThemeRepository
import javax.inject.Inject

class ThemeUseCases @Inject constructor(
    val getThemeUseCase: GetThemeUseCase,
    val setThemeUseCase: SetThemeUseCase
)

class GetThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    operator fun invoke() = repository.getThemeConfig()
}

class SetThemeUseCase @Inject constructor(
    private val repository: ThemeRepository
) {
    suspend operator fun invoke(theme: ThemeConfig) = repository.setThemeConfig(theme)
}