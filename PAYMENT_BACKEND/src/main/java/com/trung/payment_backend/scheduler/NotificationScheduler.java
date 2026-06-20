package com.trung.payment_backend.scheduler;

import com.trung.payment_backend.model.VehicleRecord;
import com.trung.payment_backend.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private FirebaseService firebaseService;

    private static final long THIRTY_MINUTES_MS = 30L * 60L * 1000L; // 1.800.000 ms

    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void checkParkingDuration() {
        List<VehicleRecord> list = firebaseService.getParkedVehicles();
        long now = System.currentTimeMillis();

        for (VehicleRecord v : list) {

            if (v.getCheckInTimeMs() <= 0) {
                // Thiếu checkInTime hợp lệ — bỏ qua, tránh tính sai
                continue;
            }

            boolean overThreshold = (now - v.getCheckInTimeMs()) > THIRTY_MINUTES_MS;

            if (overThreshold && !v.isNotified30Min()) {
                String title = "Cảnh báo gửi xe quá giờ";
                String body  = "Xe thẻ " + v.getRfidUid() + " đã gửi quá 30 phút trong bãi.";
                String uid = (v.getUserId() != null) ? v.getUserId() : "UNKNOWN_USER";
                firebaseService.sendNotification(v.getFcmToken(), title, body, uid);
                firebaseService.markNotified(v.getDocumentId());

                System.out.printf("[SCHEDULER] Đã cảnh báo quá giờ cho rfidUid=%s (doc=%s)\n",
                        v.getRfidUid(), v.getDocumentId());
            }
        }
    }
}