package com.trung.payment_backend.controller;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.trung.payment_backend.dto.HardwareEventRequest;
import com.trung.payment_backend.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hardware")
public class HardwareController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/event")
    public ResponseEntity<String> handleEvent(@RequestBody HardwareEventRequest request) {
        if (request == null || request.getType() == null || request.getRfidUid() == null) {
            return ResponseEntity.badRequest().body("Thiếu type hoặc rfidUid");
        }

        System.out.printf("[HARDWARE] Nhận sự kiện %s — rfidUid=%s, userId=%s, deviceId=%s\n",
                request.getType(), request.getRfidUid(), request.getUserId(), request.getDeviceId());

        if (request.getUserId() != null && !request.getUserId().isEmpty()) {
            firebaseService.handleHardwareEvent(request.getType(), request.getRfidUid(), request.getUserId());

            long finalFee = 0;

            if ("OUT".equalsIgnoreCase(request.getType())) {
                try {
                    Firestore db = FirestoreClient.getFirestore();
                    QuerySnapshot historySnapshot = db.collection("parking_histories")
                            .whereEqualTo("rfidUid", request.getRfidUid())
                            .whereEqualTo("status", "CHECK_OUT")
                            .orderBy("checkInTime", com.google.cloud.firestore.Query.Direction.DESCENDING)
                            .limit(1)
                            .get().get();

                    if (!historySnapshot.isEmpty()) {
                        DocumentSnapshot historyDoc = historySnapshot.getDocuments().get(0);

                        Long feeObj = historyDoc.getLong("fee");
                        finalFee = (feeObj != null) ? feeObj : 0L;

                        System.out.println("[DEBUG] Đã lấy được phí từ Firestore: " + finalFee);
                    } else {
                        System.out.println("[DEBUG] Không tìm thấy bản ghi CHECK_OUT nào cho thẻ này.");
                    }
                } catch (Exception e) {
                    System.err.println("[HARDWARE] Lỗi truy vấn phí: " + e.getMessage());
                }
            }

            System.out.println("[DEBUG] Đang đẩy phí sang Analytics: " + finalFee);
            firebaseService.updateRealtimeAnalytics(request.getType(), finalFee);

        } else {
            System.err.println("[HARDWARE] Thiếu userId");
        }

        return ResponseEntity.ok("Received");
    }

    @PostMapping("/check-card")
    public ResponseEntity<Map<String, Object>> checkCardPermission(@RequestBody Map<String, String> request) {
        String rfidUid = request.get("uid");
        Map<String, Object> response = new HashMap<>();

        try {
            Firestore db = FirestoreClient.getFirestore();

            QuerySnapshot cardSnapshot = db.collection("registered_cards")
                    .whereEqualTo("rfidUid", rfidUid).get().get();

            if (cardSnapshot.isEmpty()) {
                response.put("action", "DENY_UNKNOWN");
                return ResponseEntity.ok(response);
            }

            DocumentSnapshot cardDoc = cardSnapshot.getDocuments().get(0);
            String status = cardDoc.getString("status");
            String userId = cardDoc.getString("userId");
            String vehicleType = cardDoc.getString("vehicleType");
            if (vehicleType == null) vehicleType = "MOTORBIKE";

            if (!"ACTIVE".equalsIgnoreCase(status)) {
                response.put("action", "DENY_BLOCKED");
                return ResponseEntity.ok(response);
            }

            QuerySnapshot profileSnapshot = db.collection("profiles")
                    .whereEqualTo("uid", userId).get().get();

            if (profileSnapshot.isEmpty()) {
                response.put("action", "DENY_UNKNOWN");
                return ResponseEntity.ok(response);
            }

            DocumentSnapshot profileDoc = profileSnapshot.getDocuments().get(0);
            Long currentDebtObj = profileDoc.getLong("currentDebt");
            long currentDebt = (currentDebtObj != null) ? currentDebtObj : 0L;

            Boolean isParkingObj = profileDoc.getBoolean("isParking");
            boolean isParking = (isParkingObj != null) && isParkingObj;

            if (currentDebt > 100000) {
                response.put("action", "DENY_DEBT");
            } else {
                response.put("action", isParking ? "OPEN_OUT" : "OPEN_IN");
            }

            response.put("userId", userId);
            response.put("vehicleType", vehicleType);
            response.put("currentDebt", currentDebt);

            String profilePath = "projects/smarttrafficradar/databases/(default)/documents/profiles/" + profileDoc.getId();
            response.put("profileDocPath", profilePath);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("action", "ERROR");
            return ResponseEntity.internalServerError().body(response);
        }
    }
}