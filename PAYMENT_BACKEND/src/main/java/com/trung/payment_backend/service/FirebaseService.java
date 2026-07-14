package com.trung.payment_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.trung.payment_backend.model.VehicleRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirebaseService {

    public void clearUserDebtAndLogHistory(String uid, long amount, long transId) {
        Firestore db = FirestoreClient.getFirestore();

        try {
            ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
            QuerySnapshot querySnapshot = query.get();

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                String documentId = document.getId();

                Long currentDebt = document.getLong("currentDebt");
                if (currentDebt == null) {
                    currentDebt = 0L;
                }

                long newDebt = currentDebt - amount;
                if (newDebt < 0) {
                    newDebt = 0;
                }

                db.collection("profiles").document(documentId).update("currentDebt", newDebt).get();
                System.out.printf("[FIRESTORE] Khách trả: %dđ. Nợ cũ: %dđ -> Nợ mới: %dđ cho User: %s\n",
                        amount, currentDebt, newDebt, uid);

            } else {
                System.err.printf("[FIRESTORE] Không tìm thấy tài khoản có uid = %s để trừ nợ!\n", uid);
            }

            Map<String, Object> paymentLog = new HashMap<>();
            paymentLog.put("userId", uid);
            paymentLog.put("amount", amount); // Số tiền thực tế đã bấm trả qua MoMo
            paymentLog.put("transactionId", String.valueOf(transId));
            paymentLog.put("status", "SUCCESS");
            paymentLog.put("paymentMethod", "MOMO");
            paymentLog.put("createdAt", com.google.cloud.Timestamp.now());

            db.collection("payment_histories").document().set(paymentLog).get();
            System.out.println("[FIRESTORE] Đã lưu giao dịch vào danh sách payment_histories!");

        } catch (Exception e) {
            System.err.println("[FIRESTORE] Lỗi thao tác trừ dữ liệu nợ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean saveFcmToken(String uid, String fcmToken) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
            QuerySnapshot snapshot = query.get();

            if (snapshot.isEmpty()) {
                System.err.printf("[FCM] Không tìm thấy profile uid=%s để lưu token\n", uid);
                return false;
            }

            String documentId = snapshot.getDocuments().get(0).getId();
            db.collection("profiles").document(documentId).update("fcmToken", fcmToken).get();
            System.out.printf("[FCM] Đã lưu fcmToken cho uid=%s\n", uid);
            return true;

        } catch (Exception e) {
            System.err.println("[FCM] Lỗi lưu fcmToken: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public String getFcmTokenByUid(String uid) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
            QuerySnapshot snapshot = query.get();
            if (snapshot.isEmpty()) return null;
            return snapshot.getDocuments().get(0).getString("fcmToken");
        } catch (Exception e) {
            System.err.println("[FCM] Lỗi lấy fcmToken: " + e.getMessage());
            return null;
        }
    }

    public String getFullNameByUid(String uid) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
            QuerySnapshot snapshot = query.get();
            if (snapshot.isEmpty()) return null;
            return snapshot.getDocuments().get(0).getString("fullName");
        } catch (Exception e) {
            return null;
        }
    }


    public List<VehicleRecord> getParkedVehicles() {
        Firestore db = FirestoreClient.getFirestore();
        List<VehicleRecord> result = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> query = db.collection("parking_histories")
                    .whereEqualTo("status", "CHECK_IN")
                    .get();
            QuerySnapshot snapshot = query.get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                String rfidUid = doc.getString("rfidUid");
                String userId = doc.getString("userId");

                // LẤY THÊM LOẠI XE TỪ FIRESTORE
                String vehicleType = doc.getString("vehicleType");
                if (vehicleType == null) vehicleType = "MOTORBIKE"; // Fallback an toàn
                Long nightsObj = doc.getLong("notifiedNights");
                long notifiedNights = (nightsObj != null) ? nightsObj : 0L;
                com.google.cloud.Timestamp checkInTs = doc.getTimestamp("checkInTime");
                long checkInMs = (checkInTs != null) ? checkInTs.toDate().getTime() : 0L;

                Boolean notifiedFlag = doc.getBoolean("notified30Min");
                boolean notified = (notifiedFlag != null) && notifiedFlag;

                String fcmToken = (userId != null) ? getFcmTokenByUid(userId) : null;

                // THÊM vehicleType VÀO CONSTRUCTOR
                result.add(new VehicleRecord(doc.getId(), rfidUid, userId, checkInMs, notified, fcmToken, vehicleType, notifiedNights));
            }

        } catch (Exception e) {
            System.err.println("[SCHEDULER] Lỗi lấy danh sách xe trong bãi: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    public void markNotified(String documentId) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.collection("parking_histories").document(documentId)
                    .update("notified30Min", true).get();
            System.out.printf("[SCHEDULER] Đã đánh dấu notified30Min cho document %s\n", documentId);
        } catch (Exception e) {
            System.err.println("[SCHEDULER] Lỗi markNotified: " + e.getMessage());
        }
    }

    public void markNotifiedNights(String documentId, long nights) {
        Firestore db = FirestoreClient.getFirestore();
        try {
            db.collection("parking_histories").document(documentId)
                    .update("notifiedNights", nights).get();
            System.out.printf("[SCHEDULER] Đã cập nhật notifiedNights = %d cho document %s\n", nights, documentId);
        } catch (Exception e) {
            System.err.println("[SCHEDULER] Lỗi markNotifiedNights: " + e.getMessage());
        }
    }

    public void pushAndSaveNotification(String uid, String titleKey, String bodyKey, List<String> args) {
        if (uid == null || uid.isEmpty()) return;

        Firestore db = FirestoreClient.getFirestore();
        String fcmToken = null;
        String lang = "vi";

        try {
            ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
            QuerySnapshot snapshot = query.get();
            if (!snapshot.isEmpty()) {
                DocumentSnapshot doc = snapshot.getDocuments().get(0);
                fcmToken = doc.getString("fcmToken");
                String userLang = doc.getString("language");
                if (userLang != null && !userLang.isEmpty()) {
                    lang = userLang;
                }
            }
        } catch (Exception e) {
            System.err.println("[FCM] Lỗi truy vấn profile để lấy ngôn ngữ: " + e.getMessage());
        }

        // 2. Tự động biên dịch nội dung Default dựa trên cấu hình ngôn ngữ của user
        String titleDefault = getLocalizedText(titleKey, lang, args);
        String bodyDefault = getLocalizedText(bodyKey, lang, args);

        // 3. Ghi dữ liệu lịch sử thông báo trực quan vào Firestore
        Map<String, Object> notiData = new HashMap<>();
        notiData.put("userId", uid);
        notiData.put("titleKey", titleKey);
        notiData.put("bodyKey", bodyKey);
        notiData.put("args", args);
        notiData.put("createdAt", com.google.cloud.Timestamp.now());
        notiData.put("isRead", false);
        db.collection("notifications").add(notiData);

        // 4. Đóng gói và phát hành gói tin FCM lồng ghép song song
        if (fcmToken != null && !fcmToken.isEmpty()) {
            String argsString = String.join(",", args);

            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(titleDefault)
                            .setBody(bodyDefault)
                            .build())
                    .putData("title_loc_key", titleKey)
                    .putData("body_loc_key", bodyKey)
                    .putData("body_loc_args", argsString)
                    .build();
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("[FCM] Đã phát chuỗi thông báo tích hợp thành công: " + response);
            } catch (FirebaseMessagingException e) {
                System.err.println("[FCM] Lỗi gửi tín hiệu FCM: " + e.getMessage());
                String errorCode = String.valueOf(e.getErrorCode());
                if ("messaging/registration-token-not-registered".equals(errorCode) ||
                        "messaging/invalid-registration-token".equals(errorCode) ||
                        e.getMessage().contains("Requested entity was not found")) {

                    System.out.println("[FCM] Token đã hết hạn, tiến hành xóa token cũ của user: " + uid);
                    try {
                        ApiFuture<QuerySnapshot> query = db.collection("profiles").whereEqualTo("uid", uid).get();
                        if (!query.get().isEmpty()) {
                            String docId = query.get().getDocuments().get(0).getId();
                            db.collection("profiles").document(docId).update("fcmToken", null);
                        }
                    } catch (Exception ex) {
                        System.err.println("[FCM] Lỗi khi xóa token cũ: " + ex.getMessage());
                    }
                }
            }
        } else {
            System.out.println("[FCM] Bỏ qua gửi Push — fcmToken rỗng.");
        }
    }

    public void handleHardwareEvent(String type, String rfidUid, String userId) {
        List<String> args = java.util.Arrays.asList(rfidUid);

        if ("IN".equalsIgnoreCase(type)) {
            pushAndSaveNotification(userId, "TITLE_GATE_IN", "BODY_GATE_IN", args);
        } else {
            pushAndSaveNotification(userId, "TITLE_GATE_OUT", "BODY_GATE_OUT", args);
        }
    }

    public void updateRealtimeAnalytics(String direction, long feeAmount) {
        try {
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("parking_analytics")
                    .child(todayStr);

            // Bắt đầu Transaction để cập nhật an toàn
            ref.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    long vehiclesInLot = 0;
                    long todayInCount = 0;
                    long todayOutCount = 0;
                    long todayRevenue = 0;

                    if (mutableData.child("vehicles_in_lot").getValue() != null) {
                        vehiclesInLot = (Long) mutableData.child("vehicles_in_lot").getValue();
                    }
                    if (mutableData.child("today_in_count").getValue() != null) {
                        todayInCount = (Long) mutableData.child("today_in_count").getValue();
                    }
                    if (mutableData.child("today_out_count").getValue() != null) {
                        todayOutCount = (Long) mutableData.child("today_out_count").getValue();
                    }
                    if (mutableData.child("today_revenue").getValue() != null) {
                        todayRevenue = (Long) mutableData.child("today_revenue").getValue();
                    }

                    if ("IN".equalsIgnoreCase(direction)) {
                        vehiclesInLot++;
                        todayInCount++;
                    } else if ("OUT".equalsIgnoreCase(direction)) {
                        vehiclesInLot--;
                        if (vehiclesInLot < 0) vehiclesInLot = 0;
                        todayOutCount++;
                        todayRevenue += feeAmount;
                    }

                    mutableData.child("vehicles_in_lot").setValue(vehiclesInLot);
                    mutableData.child("today_in_count").setValue(todayInCount);
                    mutableData.child("today_out_count").setValue(todayOutCount);
                    mutableData.child("today_revenue").setValue(todayRevenue);

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (committed) {
                        System.out.printf("[ANALYTICS] Đã cập nhật thành công RTDB ngày %s hướng %s — Phí: %,d đ\n", todayStr, direction, feeAmount);
                    } else {
                        System.err.println("[ANALYTICS] Lỗi Transaction cập nhật Realtime Database: " + databaseError.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("[ANALYTICS] Lỗi ngoại lệ khi chạy Transaction: " + e.getMessage());
        }
    }

    private String getLocalizedText(String key, String lang, List<String> args) {
        boolean isEn = "en".equalsIgnoreCase(lang);
        String text = "";

        switch (key) {
            case "TITLE_GATE_IN":
                text = isEn ? "Vehicle entered" : "Xe vào cổng";
                break;
            case "BODY_GATE_IN":
                text = isEn ? "Your vehicle (card {0}) has just entered the parking lot." : "Xe của bạn (thẻ {0}) vừa vào bãi đỗ.";
                break;

            case "TITLE_GATE_OUT":
                text = isEn ? "Vehicle exited" : "Xe ra cổng";
                break;
            case "BODY_GATE_OUT":
                text = isEn ? "Your vehicle (card {0}) has just left the parking lot. Thank you!" : "Xe của bạn (thẻ {0}) vừa rời bãi đỗ. Cảm ơn bạn!"; // [cite: 3]
                break;

            case "TITLE_PARKING_OVER_30_MIN":
                text = isEn ? "Parked over 30 minutes" : "Xe gửi quá 30 phút";
                break;
            case "BODY_PARKING_OVER_30_MIN":
                text = isEn ? "{0} (Card {1}) has been parked for {2} minutes. Estimated fee: {3} đ." : "{0} (Thẻ {1}) đã gửi được {2} phút. Tạm tính phí: {3} đ."; // [cite: 3]
                break;

            case "TITLE_PARKING_OVERNIGHT":
                text = isEn ? "Overnight parking" : "Xe gửi qua đêm";
                break;
            case "BODY_PARKING_OVERNIGHT":
                text = isEn ? "Your {0} (Card {1}) has been parked overnight (since {2}). Accumulated fee: {3} đ." : "{0} (Thẻ {1}) của bạn đã gửi qua đêm (vào lúc {2}). Phí tích lũy: {3} đ."; // [cite: 3]
                break;

            case "TITLE_PAYMENT_SUCCESS":
                text = isEn ? "Payment successful" : "Thanh toán thành công";
                break;
            case "BODY_PAYMENT_SUCCESS":
                text = isEn ? "You have successfully paid {0}đ for parking services." : "Bạn đã thanh toán {0}đ cho dịch vụ gửi xe."; // [cite: 3]
                break;

            default:
                text = key;
        }

        if (args != null) {
            for (int i = 0; i < args.size(); i++) {
                String val = args.get(i);
                if (i == 0 && (key.contains("OVER_30_MIN") || key.contains("OVERNIGHT"))) {
                    val = "CAR".equalsIgnoreCase(val) ? (isEn ? "Car" : "Ô tô") : (isEn ? "Motorbike" : "Xe máy");
                }
                text = text.replace("{" + i + "}", val);
            }
        }
        return text;
    }
}
