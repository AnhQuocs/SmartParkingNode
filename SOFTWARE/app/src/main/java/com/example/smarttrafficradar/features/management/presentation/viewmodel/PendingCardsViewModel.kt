package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.PendingCard
import com.example.smarttrafficradar.features.management.domain.model.PendingCardStatus
import com.example.smarttrafficradar.features.management.domain.usecase.cards.GetPendingCardsUseCase
import com.example.smarttrafficradar.features.management.domain.usecase.cards.UpdatePendingCardStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PendingCardsState {
    data object Loading : PendingCardsState()
    data class Success(val cards: List<PendingCard>) : PendingCardsState()
    data class Error(val message: String) : PendingCardsState()
}

@HiltViewModel
class PendingCardsViewModel @Inject constructor(
    private val getPendingCardsUseCase: GetPendingCardsUseCase,
    private val updatePendingCardStatusUseCase: UpdatePendingCardStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PendingCardsState>(PendingCardsState.Loading)
    val state = _state.asStateFlow()

    init {
        getPendingCards()
    }

    private fun getPendingCards() {
        viewModelScope.launch {
            getPendingCardsUseCase()
                .map { cards -> 
                    // Chỉ hiển thị các thẻ đang PENDING cho Admin
                    cards.filter { it.status == PendingCardStatus.PENDING } 
                }
                .onStart {
                    _state.value = PendingCardsState.Loading
                }
                .catch { e ->
                    _state.value = PendingCardsState.Error(e.message ?: "An unknown error occurred")
                }
                .collect { cards ->
                    _state.value = PendingCardsState.Success(cards)
                }
        }
    }

    fun approveCard(uid: String) {
        viewModelScope.launch {
            updatePendingCardStatusUseCase(uid, PendingCardStatus.APPROVED)
        }
    }

    fun rejectCard(uid: String) {
        viewModelScope.launch {
            updatePendingCardStatusUseCase(uid, PendingCardStatus.REJECTED)
        }
    }
}
