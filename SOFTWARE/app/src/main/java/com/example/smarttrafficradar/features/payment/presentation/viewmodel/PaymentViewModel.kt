package com.example.smarttrafficradar.features.payment.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smarttrafficradar.features.payment.domain.usecase.CreateMomoUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val payUrl: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val createMomoUrlUseCase: CreateMomoUrlUseCase
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun createPaymentUrl(uid: String, amount: Int) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading

            val result = createMomoUrlUseCase(uid, amount)

            result.fold(
                onSuccess = { paymentInfo ->
                    _paymentState.value = PaymentState.Success(paymentInfo.payUrl)
                },
                onFailure = { exception ->
                    _paymentState.value =
                        PaymentState.Error(exception.message ?: "Unknown Error")
                }
            )
        }
    }

    fun resetState() {
        _paymentState.value = PaymentState.Idle
    }
}