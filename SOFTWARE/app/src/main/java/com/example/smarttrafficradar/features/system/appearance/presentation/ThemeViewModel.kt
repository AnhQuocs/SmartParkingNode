package com.example.smarttrafficradar.features.system.appearance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.system.appearance.domain.model.ThemeConfig
import com.example.smarttrafficradar.features.system.appearance.domain.usecase.ThemeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeUseCases: ThemeUseCases
) : ViewModel() {

    val themeState: StateFlow<ThemeConfig> = themeUseCases.getThemeUseCase()
        .stateIn(viewModelScope, SharingStarted.Companion.WhileSubscribed(5000), ThemeConfig.FOLLOW_SYSTEM)

    fun onThemeChange(newTheme: ThemeConfig) {
        viewModelScope.launch {
            themeUseCases.setThemeUseCase(newTheme)
        }
    }
}