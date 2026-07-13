package com.example.smarttrafficradar.features.management.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.management.domain.model.CardStatus
import com.example.smarttrafficradar.features.management.domain.model.RegisteredCard
import com.example.smarttrafficradar.features.management.domain.model.RegistrationRequest
import com.example.smarttrafficradar.features.management.domain.usecase.RegistrationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegistrationListState {
    data object Loading : RegistrationListState()
    data class Success(
        val requests: List<RegistrationRequest>,
        val cards: List<RegisteredCard>,
        val filteredCards: List<RegisteredCard> = emptyList()
    ) : RegistrationListState()
    data class Error(val message: String) : RegistrationListState()
}

@HiltViewModel
class RegistrationListViewModel @Inject constructor(
    private val registrationUseCases: RegistrationUseCases
) : ViewModel() {

    private val _state = MutableStateFlow<RegistrationListState>(RegistrationListState.Loading)
    val state = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedStatus = MutableStateFlow<CardStatus?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()

    init {
        getData()
    }

    @OptIn(FlowPreview::class)
    private fun getData() {
        viewModelScope.launch {
            combine(
                registrationUseCases.getRegistrationRequests(),
                registrationUseCases.getRegisteredCards(),
                _searchQuery.debounce(300),
                _selectedStatus
            ) { requests, cards, query, status ->
                val filtered = cards.filter { card ->
                    val matchesQuery = card.ownerName.contains(query, ignoreCase = true) ||
                            card.rfidUid.contains(query, ignoreCase = true)
                    val matchesStatus = status == null || card.status == status
                    matchesQuery && matchesStatus
                }

                RegistrationListState.Success(
                    requests = requests.sortedByDescending { it.timestamp },
                    cards = cards,
                    filteredCards = filtered
                )
            }
                .onStart {
                    _state.value = RegistrationListState.Loading
                }
                .catch { e ->
                    _state.value = RegistrationListState.Error(
                        e.message ?: "An unknown error occurred"
                    )
                }
                .collect { newState ->
                    _state.value = newState
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChange(status: CardStatus?) {
        _selectedStatus.value = status
    }

    fun approveRequest(uid: String) {
        viewModelScope.launch {
            try {
                registrationUseCases.approveRegistration(uid)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun rejectRequest(uid: String) {
        viewModelScope.launch {
            try {
                registrationUseCases.rejectRegistration(uid)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}
