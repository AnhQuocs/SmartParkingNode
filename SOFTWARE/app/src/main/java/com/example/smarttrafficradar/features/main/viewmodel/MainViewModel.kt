package com.example.smarttrafficradar.features.main.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.app_system.appearance.domain.model.ThemeConfig
import com.example.smarttrafficradar.features.app_system.appearance.domain.usecase.GetThemeUseCase
import com.example.smarttrafficradar.features.auth.domain.usecase.UpdateFcmTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : ViewModel() {

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

//    init {
//        viewModelScope.launch {
//            delay(800L)
//            _isReady.value = true
//        }
//    }

    val themeConfig: StateFlow<ThemeConfig> = getThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeConfig.FOLLOW_SYSTEM
        )

    fun sendFcmTokenToServer(uid: String, token: String) {
        viewModelScope.launch {
            val result = updateFcmTokenUseCase(uid, token)
            result.fold(
                onSuccess = {
                    Log.d("FCM_SYNC", "Đã gửi FCM Token lên server thành công!")
                },
                onFailure = { e ->
                    Log.e("FCM_SYNC", "Lỗi gửi FCM Token: ${e.message}")
                }
            )
        }
    }
}