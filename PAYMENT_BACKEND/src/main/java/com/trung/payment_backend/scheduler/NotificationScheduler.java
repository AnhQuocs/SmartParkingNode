package com.trung.payment_backend.scheduler;

import com.trung.payment_backend.model.VehicleRecord;
import com.trung.payment_backend.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private FirebaseService firebaseService;

    private static final long THIRTY_SECOND_MS = 30L * 1000L;
//    private static final long THIRTY_MINUTE_MS = 60L * 30L * 1000L;

    @Scheduled(fixedRate = 10000) // Chạy mỗi 10s để quét
    public void checkParkingDuration() {
        List<VehicleRecord> list = firebaseService.getParkedVehicles();
        long now = System.currentTimeMillis();

        for (VehicleRecord v : list) {
            if (v.getCheckInTime() <= 0) continue;

            boolean overThreshold = (now - v.getCheckInTime()) > THIRTY_SECOND_MS;

            if (overThreshold && !v.isNotified30Min()) {
                // 1. Tính toán thời gian và mức phí
                long durationMinutes = (long) Math.ceil((double) (now - v.getCheckInTime()) / 60000.0);
                long currentFee = calculateFee(v.getCheckInTime(), now, v.getVehicleType());

                String uid = (v.getUserId() != null) ? v.getUserId() : "UNKNOWN_USER";
                List<String> args = Arrays.asList(
                        v.getVehicleType(),
                        v.getRfidUid(),
                        String.valueOf(durationMinutes),
                        String.valueOf(currentFee)
                );

                firebaseService.pushAndSaveNotification(
                        uid,
                        "TITLE_PARKING_OVER_30_MIN",
                        "BODY_PARKING_OVER_30_MIN",
                        args
                );

                firebaseService.markNotified(v.getDocumentId());

                System.out.printf("[SCHEDULER] Đã cảnh báo xe %s quá giờ (doc=%s) - Phí: %d\n",
                        v.getRfidUid(), v.getDocumentId(), currentFee);
            }
        }
    }

    private long calculateFee(long checkInMs, long nowMs, String vehicleType) {
        long durationMin = (long) Math.ceil((double) (nowMs - checkInMs) / 60000.0);
//        if (durationMin < 30) return 0;
//        long durationSec = (nowMs - checkInMs) / 1000;
//        if (durationSec <= 10) return 0;

        long inLocal = (checkInMs / 1000) + 7 * 3600;
        long outLocal = (nowMs / 1000) + 7 * 3600;

        long inDay = inLocal / 86400;
        long outDay = outLocal / 86400;
        long overnightCount = outDay - inDay;

        if (overnightCount > 0) {
            if ("CAR".equalsIgnoreCase(vehicleType)) return 50000L * overnightCount;
            return 15000L * overnightCount;
        } else {
            if ("CAR".equalsIgnoreCase(vehicleType)) return 20000L;
            return 5000L;
        }
    }

    @Scheduled(fixedRate = 10000) // Bạn đang để 10s để test
    public void checkOvernightParking() {
        List<VehicleRecord> list = firebaseService.getParkedVehicles();
        long now = System.currentTimeMillis();

        for (VehicleRecord v : list) {
            if (v.getCheckInTime() <= 0) continue;

            long inLocal = (v.getCheckInTime() / 1000) + 7 * 3600;
            long outLocal = (now / 1000) + 7 * 3600;

            long inDay = inLocal / 86400;
            long outDay = outLocal / 86400;
            long overnightCount = outDay - inDay;

            if (overnightCount > v.getNotifiedNights()) {
                long currentFee = calculateFee(v.getCheckInTime(), now, v.getVehicleType());

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
                String checkInStr = sdf.format(new java.util.Date(v.getCheckInTime()));

                String uid = (v.getUserId() != null) ? v.getUserId() : "UNKNOWN_USER";

                List<String> args = Arrays.asList(
                        v.getVehicleType(),
                        v.getRfidUid(),
                        checkInStr,
                        String.valueOf(currentFee)
                );

                firebaseService.pushAndSaveNotification(
                        uid,
                        "TITLE_PARKING_OVERNIGHT",
                        "BODY_PARKING_OVERNIGHT",
                        args
                );

                firebaseService.markNotifiedNights(v.getDocumentId(), overnightCount);

                System.out.printf("[OVERNIGHT-SCHEDULER] Đã gửi thông báo qua đêm cho rfidUid=%s, Số đêm=%d\n",
                        v.getRfidUid(), overnightCount);
            }
        }
    }
}