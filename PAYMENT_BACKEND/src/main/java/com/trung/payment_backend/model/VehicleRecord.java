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
    private long checkInTime;
    private boolean notified30Min;
    private String fcmToken;
    private String vehicleType;
    private long notifiedNights;
}