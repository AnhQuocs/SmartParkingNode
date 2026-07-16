package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.OrganizationMember
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.usecase.GetOrganizationMembersUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MemberInfoState {
    data object Idle : MemberInfoState()
    data object Loading : MemberInfoState()
    data class Success(val card: RegisteredCard) : MemberInfoState()
    data class Unregistered(val member: OrganizationMember) : MemberInfoState()
    data class Error(val message: String) : MemberInfoState()
}

@HiltViewModel
class MemberInfoViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCases,
    private val getOrganizationMembersUseCase: GetOrganizationMembersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MemberInfoState>(MemberInfoState.Idle)
    val state = _state.asStateFlow()

    fun getMemberInfo(identifier: String) {
        viewModelScope.launch {
            _state.value = MemberInfoState.Loading
            
            combine(
                registrationUseCases.getRegisteredCards(),
                getOrganizationMembersUseCase()
            ) { cards, members ->
                val card = cards.find { it.identifier == identifier }
                val member = members.find { it.identifier == identifier }
                
                when {
                    card != null -> MemberInfoState.Success(card)
                    member != null -> MemberInfoState.Unregistered(member)
                    else -> MemberInfoState.Error("Member not found")
                }
            }.catch { e ->
                _state.value = MemberInfoState.Error(e.message ?: "Unknown error")
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun updateCardStatus(cardId: String, status: CardStatus) {
        viewModelScope.launch {
            try {
                registrationUseCases.updateCardStatus(cardId, status)
            } catch (e: Exception) {
                _state.value = MemberInfoState.Error("Cannot update status: ${e.message}")
            }
        }
    }
}
