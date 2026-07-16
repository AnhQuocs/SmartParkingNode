package com.example.smarttrafficradar.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.auth.domain.model.AuthError
import com.example.smarttrafficradar.features.auth.domain.model.AuthUser
import com.example.smarttrafficradar.features.auth.domain.usecase.AuthUseCases
import com.example.smarttrafficradar.features.auth.domain.usecase.ChangePasswordUseCase
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: AuthUser) : AuthState()
    data object SignedOut : AuthState()
    data class Error(val message: UiText) : AuthState()
    data object PasswordChanged : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            authUseCases.getCurrentUserUseCase().collect { user ->
                _currentUser.value = user
                if (user != null) {
                    _state.value = AuthState.Success(user)
                } else {
                    if (_state.value !is AuthState.Idle) {
                        _state.value = AuthState.SignedOut
                    }
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val user = authUseCases.signInUseCase(email, password)
                _state.value = AuthState.Success(user)
            } catch (e: AuthError) {
                _state.value = AuthState.Error(mapAuthErrorToUiText(e))
            } catch (e: Exception) {
                _state.value = AuthState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val user = authUseCases.signUpUseCase(username, email, password)
                _state.value = AuthState.Success(user)
            } catch (e: AuthError) {
                _state.value = AuthState.Error(mapAuthErrorToUiText(e))
            } catch (e: Exception) {
                _state.value = AuthState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    fun signUpAdmin(username: String, email: String, password: String, adminCode: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val user = authUseCases.signUpAdminUseCase(username, email, password, adminCode)
                _state.value = AuthState.Success(user)
            } catch (e: AuthError) {
                _state.value = AuthState.Error(mapAuthErrorToUiText(e))
            } catch (e: Exception) {
                _state.value = AuthState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                changePasswordUseCase(oldPassword, newPassword)
                _state.value = AuthState.PasswordChanged
            } catch (e: AuthError) {
                _state.value = AuthState.Error(mapAuthErrorToUiText(e))
            } catch (e: Exception) {
                _state.value = AuthState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                authUseCases.signOutUseCase()
                _state.value = AuthState.SignedOut
            } catch (e: Exception) {
                _state.value = AuthState.Error(UiText.StringResource(R.string.error_unexpected))
            }
        }
    }

    fun clearError() {
        _state.value = if (_currentUser.value != null) {
            AuthState.Success(_currentUser.value!!)
        } else {
            AuthState.Idle
        }
    }

    private fun mapAuthErrorToUiText(error: AuthError): UiText {
        return when (error) {
            is AuthError.EmptyUsername -> UiText.StringResource(R.string.error_empty_username)
            is AuthError.InvalidEmail -> UiText.StringResource(R.string.error_invalid_email)
            is AuthError.EmptyPassword -> UiText.StringResource(R.string.error_empty_password)
            is AuthError.PasswordTooShort -> UiText.StringResource(R.string.error_password_too_short)
            is AuthError.EmptyAdminCode -> UiText.StringResource(R.string.error_empty_admin_code)
            is AuthError.UserNotFound -> UiText.StringResource(R.string.error_user_not_found)
            is AuthError.WrongPassword -> UiText.StringResource(R.string.error_wrong_password)
            is AuthError.EmailAlreadyInUse -> UiText.StringResource(R.string.error_email_already_in_use)
            is AuthError.InvalidAdminCode -> UiText.StringResource(R.string.error_invalid_admin_code)
            is AuthError.RemoteError -> {
                if (error.message != null) UiText.DynamicString(error.message)
                else UiText.StringResource(R.string.error_auth_failed)
            }
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    }
}
