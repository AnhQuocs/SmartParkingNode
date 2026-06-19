package com.example.smarttrafficradar.features.user_profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfile
import com.example.smarttrafficradar.features.user_profile.domain.model.UserProfileError
import com.example.smarttrafficradar.features.user_profile.domain.usecase.UserProfileUseCases
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfileState {
    data object Idle : UserProfileState()
    data object Loading : UserProfileState()
    data class Success(val profile: UserProfile) : UserProfileState()
    data class Error(val uiText: UiText) : UserProfileState()
}

data class OrganizationMemberInfo(
    val fullName: String,
    val department: String
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileUseCases: UserProfileUseCases
) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfileState>(UserProfileState.Idle)
    val profileState: StateFlow<UserProfileState> = _profileState.asStateFlow()

    private val _identifierError = MutableStateFlow<UiText?>(null)
    val identifierError: StateFlow<UiText?> = _identifierError.asStateFlow()

    private val _memberInfo = MutableStateFlow<OrganizationMemberInfo?>(null)
    val memberInfo: StateFlow<OrganizationMemberInfo?> = _memberInfo.asStateFlow()

    fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            _profileState.value = UserProfileState.Loading
            userProfileUseCases.getUserProfile(uid).collect { profile ->
                if (profile != null) {
                    _profileState.value = UserProfileState.Success(profile)
                } else {
                    _profileState.value = UserProfileState.Error(UiText.StringResource(R.string.error_profile_not_found))
                }
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _profileState.value = UserProfileState.Loading
            val result = userProfileUseCases.saveUserProfile(profile)
            result.onSuccess {
                _profileState.value = UserProfileState.Success(profile)
            }.onFailure { exception ->
                _profileState.value = UserProfileState.Error(mapErrorToUiText(exception))
            }
        }
    }

    /**
     * Kiểm tra mã sinh viên/nhân viên và Email (Check lần 1)
     * Nếu hợp lệ, tự động lấy thông tin fullName và department
     */
    fun validateAndFetchMember(identifier: String, email: String) {
        viewModelScope.launch {
            val result = userProfileUseCases.checkIdentifierExists(identifier, email)
            result.onSuccess {
                _identifierError.value = null
                // Lấy thông tin từ organization_members
                val data = userProfileUseCases.getOrganizationMember(identifier, email)
                if (data != null) {
                    _memberInfo.value = OrganizationMemberInfo(
                        fullName = data["fullName"] as? String ?: "",
                        department = data["department"] as? String ?: ""
                    )
                }
            }.onFailure { exception ->
                _identifierError.value = mapErrorToUiText(exception)
                _memberInfo.value = null
            }
        }
    }

    fun clearError() {
        _profileState.value = UserProfileState.Idle
        _identifierError.value = null
    }

    private fun mapErrorToUiText(e: Throwable): UiText {
        return when (e) {
            is UserProfileError.IdentifierAlreadyExists -> UiText.StringResource(R.string.error_identifier_exists)
            is UserProfileError.IdentifierNotFound -> UiText.StringResource(R.string.error_identifier_not_found)
            is UserProfileError.ProfileNotFound -> UiText.StringResource(R.string.error_profile_not_found)
            is UserProfileError.DebtLimitExceeded -> UiText.StringResource(R.string.error_debt_limit)
            is UserProfileError.InvalidFullName -> UiText.StringResource(R.string.error_invalid_fullname)
            is UserProfileError.NetworkError -> UiText.DynamicString("Network error, please try again")
            is UserProfileError.UnknownError -> UiText.DynamicString(e.msg)
            else -> UiText.StringResource(R.string.error_unexpected)
        }
    }
}
