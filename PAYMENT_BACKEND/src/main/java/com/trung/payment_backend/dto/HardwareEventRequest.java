package com.trung.payment_backend.dto;

import lombok.Data;

@Data
public class HardwareEventRequest {
    private String type;
    private String rfidUid;
    private String userId;
    private String deviceId;

}