package com.example.smarttrafficradar.features.app_system.language.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.app_system.language.domain.model.AppLanguage
import com.example.smarttrafficradar.features.app_system.language.domain.usecase.GetLanguageUseCase
import com.example.smarttrafficradar.features.app_system.language.domain.usecase.UpdateLanguageUseCase
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import com.example.smarttrafficradar.features.user_profile.domain.model.UserLang
import com.example.smarttrafficradar.features.user_profile.domain.usecase.UpdateUserProfileLanguageUseCase
import com.example.smarttrafficradar.utils.LangUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val getLanguageUseCase: GetLanguageUseCase,
    private val updateLanguageUseCase: UpdateLanguageUseCase,
    private val updateUserProfileLanguageUseCase: UpdateUserProfileLanguageUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            getLanguageUseCase().collect {
                LangUtils.currentLang = it.code
                _currentLanguage.value = it
            }
        }
    }

    fun changeLanguage(language: AppLanguage) {
        viewModelScope.launch {
            // 1. Cập nhật ngôn ngữ trong DataStore (App-level)
            updateLanguageUseCase(language)

            // 2. Cập nhật ngôn ngữ trong UserProfile nếu user đã đăng nhập
            val user = authRepository.getCurrentUser().first()
            user?.let {
                val userLang = when (language) {
                    AppLanguage.ENGLISH -> UserLang.EN
                    AppLanguage.VIETNAMESE -> UserLang.VI
                }
                updateUserProfileLanguageUseCase(it.uid, userLang)
            }
        }
    }
}
