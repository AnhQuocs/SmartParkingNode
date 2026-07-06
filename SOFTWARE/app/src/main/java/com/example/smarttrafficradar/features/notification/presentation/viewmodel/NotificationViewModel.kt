package com.example.smarttrafficradar.features.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.auth.domain.repository.AuthRepository
import com.example.smarttrafficradar.features.notification.domain.model.Notification
import com.example.smarttrafficradar.features.notification.domain.usecase.GetNotificationsUseCase
import com.example.smarttrafficradar.features.notification.domain.usecase.MarkAllNotificationsAsReadUseCase
import com.example.smarttrafficradar.features.notification.domain.usecase.MarkNotificationAsReadUseCase
import com.example.smarttrafficradar.features.notification.presentation.ui.NotificationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    private val _selectedNotification = MutableStateFlow<Notification?>(null)
    val selectedNotification: StateFlow<Notification?> = _selectedNotification.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.getCurrentUser().collectLatest { user ->
                if (user != null) {
                    getNotificationsUseCase(user.uid).collectLatest { notifications ->
                        _state.update { 
                            it.copy(
                                notifications = notifications,
                                isLoading = false,
                                error = null
                            ) 
                        }
                    }
                } else {
                    _state.update { 
                        it.copy(
                            isLoading = false, 
                            error = "User not logged in" 
                        ) 
                    }
                }
            }
        }
    }

    fun selectNotification(notification: Notification) {
        _selectedNotification.value = notification
        if (!notification.isRead) {
            markAsRead(notification.id)
        }
    }

    fun clearSelection() {
        _selectedNotification.value = null
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collectLatest { user ->
                user?.let {
                    markAllNotificationsAsReadUseCase(it.uid)
                }
            }
        }
    }

    private fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(id)
        }
    }
}
