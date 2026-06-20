package com.trung.payment_backend.dto;

import lombok.Data;

@Data
public class FcmTokenRequest {
    private String uid;
    private String fcmToken;
}
