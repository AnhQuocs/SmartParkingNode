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

    // Đang để 30s để test, lúc chạy thật nhớ đổi về 30 phút (1.800.000 ms)
    private static final long THIRTY_SECOND_MS = 60L * 1000L;

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
                String typeDisplay = "CAR".equalsIgnoreCase(v.getVehicleType()) ? "Ô tô" : "Xe máy";

                // 2. Tạo nội dung thông báo linh hoạt
                String title = "Xe gửi quá 30 phút";
                String body = String.format("%s (Thẻ %s) đã gửi được %d phút. Tạm tính phí: %,d đ.",
                        typeDisplay, v.getRfidUid(), durationMinutes, currentFee);

                String uid = (v.getUserId() != null) ? v.getUserId() : "UNKNOWN_USER";

                // 3. Gửi thông báo
                firebaseService.pushAndSaveNotification(uid, title, body);
                firebaseService.markNotified(v.getDocumentId());

                System.out.printf("[SCHEDULER] Đã cảnh báo xe %s quá giờ (doc=%s) - Phí: %d\n",
                        v.getRfidUid(), v.getDocumentId(), currentFee);
            }
        }
    }

    private long calculateFee(long checkInMs, long nowMs, String vehicleType) {
        long durationMin = (long) Math.ceil((double) (nowMs - checkInMs) / 60000.0);
        if (durationMin < 30) return 0;
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

            // KIỂM TRA: Chỉ thông báo nếu số đêm thực tế lớn hơn số đêm đã từng báo
            if (overnightCount > v.getNotifiedNights()) {
                long currentFee = calculateFee(v.getCheckInTime(), now, v.getVehicleType());
                String typeDisplay = "CAR".equalsIgnoreCase(v.getVehicleType()) ? "Ô tô" : "Xe máy";

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+7"));
                String checkInStr = sdf.format(new java.util.Date(v.getCheckInTime()));

                String title = "Thông báo: Xe gửi qua đêm";
                String body = String.format("%s (Thẻ %s) của bạn đã gửi qua đêm (vào lúc %s). Phí tích lũy: %,d đ.",
                        typeDisplay, v.getRfidUid(), checkInStr, currentFee);

                String uid = (v.getUserId() != null) ? v.getUserId() : "UNKNOWN_USER";

                firebaseService.pushAndSaveNotification(uid, title, body);

                // ĐÁNH DẤU LÀ ĐÃ THÔNG BÁO CHO SỐ ĐÊM NÀY
                firebaseService.markNotifiedNights(v.getDocumentId(), overnightCount);

                System.out.printf("[OVERNIGHT-SCHEDULER] Đã gửi thông báo qua đêm cho rfidUid=%s, Số đêm=%d\n",
                        v.getRfidUid(), overnightCount);
            }
        }
    }

}