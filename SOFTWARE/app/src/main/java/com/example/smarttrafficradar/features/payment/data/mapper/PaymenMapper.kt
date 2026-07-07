package com.example.smarttrafficradar.features.payment.data.mapper

import com.example.smarttrafficradar.features.payment.data.remote.dto.PaymentHistoryDto
import com.example.smarttrafficradar.features.payment.domain.model.PaymentHistory

fun PaymentHistoryDto.toDomain() = PaymentHistory(
    id = id.orEmpty(),
    userId = userId.orEmpty(),
    amount = amount,
    method = method,
    status = status,
    createdAt = createdAt?.toDate()?.time ?: 0L
)