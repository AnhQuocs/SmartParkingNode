package com.example.smarttrafficradar.features.analytics.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory
import com.example.smarttrafficradar.features.payment.domain.usecase.GetAllTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

data class AnalyticsState(
    val transactions: List<PaymentHistory> = emptyList(),
    val filteredTransactions: List<PaymentHistory> = emptyList(),
    val totalRevenue: Long = 0,
    val selectedPeriod: Period = Period.DAILY,
    val isLoading: Boolean = false,
    val startDate: Long? = null,
    val endDate: Long? = null
)

enum class Period { DAILY, WEEKLY, MONTHLY, CUSTOM }

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state.asStateFlow()

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        _state.value = _state.value.copy(isLoading = true)
        getAllTransactionsUseCase()
            .onEach { transactions ->
                _state.value = _state.value.copy(
                    transactions = transactions,
                    isLoading = false
                )
                applyFilter()
            }
            .launchIn(viewModelScope)
    }

    fun setPeriod(period: Period) {
        _state.value = _state.value.copy(selectedPeriod = period)
        applyFilter()
    }

    fun setCustomRange(start: Long?, end: Long?) {
        _state.value = _state.value.copy(
            selectedPeriod = Period.CUSTOM,
            startDate = start,
            endDate = end
        )
        applyFilter()
    }

    private fun applyFilter() {
        val currentState = _state.value
        
        val filtered = when (currentState.selectedPeriod) {
            Period.DAILY -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -6)
                resetTime(cal)
                currentState.transactions.filter { it.createdAt >= cal.timeInMillis }
            }
            Period.WEEKLY -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.WEEK_OF_YEAR, -3)
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                resetTime(cal)
                currentState.transactions.filter { it.createdAt >= cal.timeInMillis }
            }
            Period.MONTHLY -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.MONTH, -11)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                resetTime(cal)
                currentState.transactions.filter { it.createdAt >= cal.timeInMillis }
            }
            Period.CUSTOM -> {
                if (currentState.startDate != null && currentState.endDate != null) {
                    currentState.transactions.filter { it.createdAt in currentState.startDate..currentState.endDate }
                } else {
                    currentState.transactions
                }
            }
        }

        _state.value = currentState.copy(
            filteredTransactions = filtered,
            totalRevenue = filtered.sumOf { it.amount.toLong() }
        )
    }

    private fun resetTime(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }
}
