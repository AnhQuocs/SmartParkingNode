package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.domain.usecase.GetOrganizationMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OrganizationMemberListState {
    data object Loading : OrganizationMemberListState()
    data class Success(val members: List<OrganizationMember>) : OrganizationMemberListState()
    data class Error(val message: String) : OrganizationMemberListState()
}

@HiltViewModel
class OrganizationMemberListViewModel @Inject constructor(
    private val getOrganizationMembersUseCase: GetOrganizationMembersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<OrganizationMemberListState>(OrganizationMemberListState.Loading)
    val state = _state.asStateFlow()

    init {
        getMembers()
    }

    private fun getMembers() {
        viewModelScope.launch {
            getOrganizationMembersUseCase()
                .onStart {
                    _state.value = OrganizationMemberListState.Loading
                }
                .catch { e ->
                    _state.value = OrganizationMemberListState.Error(
                        e.message ?: "An unknown error occurred"
                    )
                }
                .collect { members ->
                    _state.value = OrganizationMemberListState.Success(members)
                }
        }
    }
}
