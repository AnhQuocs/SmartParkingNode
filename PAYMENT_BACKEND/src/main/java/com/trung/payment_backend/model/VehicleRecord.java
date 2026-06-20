package com.trung.payment_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRecord {
    private String documentId;
    private String rfidUid;
    private String userId;
    private long checkInTimeMs;
    private boolean notified30Min;
    private String fcmToken;

}