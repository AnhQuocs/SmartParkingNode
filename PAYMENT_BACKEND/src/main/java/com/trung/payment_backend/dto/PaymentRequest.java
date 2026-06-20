package com.trung.payment_backend.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String uid;
    private long amount;
}
