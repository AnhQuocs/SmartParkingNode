package com.example.smarttrafficradar.features.history.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.R
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistory
import com.example.smarttrafficradar.features.history.domain.model.ParkingHistoryError
import com.example.smarttrafficradar.features.history.domain.usecase.ParkingHistoryUseCases
import com.example.smarttrafficradar.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ParkingHistoryState {
    data object Idle : ParkingHistoryState()
    data object Loading : ParkingHistoryState()
    data class Success(val histories: List<ParkingHistory>) : ParkingHistoryState()
    data class Error(val uiText: UiText) : ParkingHistoryState()
}

sealed class HistoryDetailState {
    data object Idle : HistoryDetailState()
    data object Loading : HistoryDetailState()
    data class Success(val history: ParkingHistory) : HistoryDetailState()
    data class Error(val uiText: UiText) : HistoryDetailState()
}

@HiltViewModel
class ParkingHistoryViewModel @Inject constructor(
    private val historyUseCases: ParkingHistoryUseCases
) : ViewModel() {

    private val _historyState =
        MutableStateFlow<ParkingHistoryState>(ParkingHistoryState.Idle)
    val historyState: StateFlow<ParkingHistoryState> =
        _historyState.asStateFlow()

    private val _detailState =
        MutableStateFlow<HistoryDetailState>(HistoryDetailState.Idle)
    val detailState: StateFlow<HistoryDetailState> =
        _detailState.asStateFlow()

    private companion object {
        const val TAG = "ParkingHistoryVM"
    }

    fun observeHistories(userId: String) {
        viewModelScope.launch {
            Log.d(TAG, "observeHistories() - userId: $userId")

            _historyState.value = ParkingHistoryState.Loading

            historyUseCases.observeHistoriesByUserIdUseCase(userId)
                .catch { exception ->
                    Log.e(
                        TAG,
                        "observeHistories() - Error: ${exception.message}",
                        exception
                    )

                    _historyState.value =
                        ParkingHistoryState.Error(
                            mapErrorToUiText(exception)
                        )
                }
                .collect { histories ->
                    Log.d(
                        TAG,
                        "observeHistories() - Received ${histories.size} histories"
                    )

                    histories.forEachIndexed { index, history ->
                        Log.d(TAG, "History[$index]: $history")
                    }

                    _historyState.value =
                        ParkingHistoryState.Success(histories)
                }
        }
    }

    fun getHistoryDetail(historyId: String) {
        viewModelScope.launch {
            _detailState.value = HistoryDetailState.Loading
            Log.d(TAG, "Loading history detail: historyId = $historyId")

            try {
                val history = historyUseCases.getHistoryDetailUseCase(historyId)

                Log.d(TAG, "History loaded successfully: $history")

                _detailState.value = HistoryDetailState.Success(history)
            } catch (exception: Exception) {
                Log.e(TAG, "Failed to load history detail", exception)

                _detailState.value = HistoryDetailState.Error(
                    mapErrorToUiText(exception)
                )
            }
        }
    }

    fun clearError() {
        _historyState.value = ParkingHistoryState.Idle
        _detailState.value = HistoryDetailState.Idle
    }

    fun clearDetailState() {
        _detailState.value = HistoryDetailState.Idle
    }

    private fun mapErrorToUiText(
        throwable: Throwable
    ): UiText {
        return when (throwable) {
            is ParkingHistoryError.HistoryNotFound -> UiText.StringResource(R.string.error_history_not_found)

            is ParkingHistoryError.InvalidHistoryData -> UiText.StringResource(R.string.error_invalid_history_data)

            is ParkingHistoryError.InvalidHistoryId -> UiText.StringResource(R.string.error_invalid_history_id)

            is ParkingHistoryError.InvalidUserId -> UiText.StringResource(R.string.error_invalid_user_id)

            is ParkingHistoryError.PermissionDenied -> UiText.StringResource(R.string.error_permission_denied)

            is ParkingHistoryError.NetworkError -> UiText.DynamicString("Network error, please try again")

            is ParkingHistoryError.UnknownError ->
                UiText.DynamicString(
                    throwable.msg
                )

            else ->
                UiText.StringResource(
                    R.string.error_unexpected
                )
        }
    }
}