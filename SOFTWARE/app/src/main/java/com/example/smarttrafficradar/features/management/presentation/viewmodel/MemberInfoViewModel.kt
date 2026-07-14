package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MemberInfoState {
    data object Idle : MemberInfoState()
    data object Loading : MemberInfoState()
    data class Success(val card: RegisteredCard) : MemberInfoState()
    data class Error(val message: String) : MemberInfoState()
}

@HiltViewModel
class MemberInfoViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<MemberInfoState>(MemberInfoState.Idle)
    val state = _state.asStateFlow()

    fun getRegisteredCard(userId: String) {
        viewModelScope.launch {
            _state.value = MemberInfoState.Loading
            registrationUseCases.getRegisteredCards()
                .catch { e ->
                    _state.value = MemberInfoState.Error(e.message ?: "Unknown error")
                }
                .collect { cards ->
                    val card = cards.find { it.userId == userId }
                    if (card != null) {
                        _state.value = MemberInfoState.Success(card)
                    } else {
                        _state.value = MemberInfoState.Error("Card not found")
                    }
                }
        }
    }

    fun updateCardStatus(cardId: String, status: CardStatus) {
        viewModelScope.launch {
            try {
                registrationUseCases.updateCardStatus(cardId, status)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
