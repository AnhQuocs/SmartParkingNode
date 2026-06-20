package com.trung.payment_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.trung.payment_backend.model.VehicleRecord;
import org.springframework.stereotype.Service;

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

                // checkInTime trong Firestore là Timestamp (theo mẫu JSON bạn gửi)
                com.google.cloud.Timestamp checkInTs = doc.getTimestamp("checkInTime");
                long checkInMs = (checkInTs != null) ? checkInTs.toDate().getTime() : 0L;

                Boolean notifiedFlag = doc.getBoolean("notified30Min");
                boolean notified = (notifiedFlag != null) && notifiedFlag;

                String fcmToken = (userId != null) ? getFcmTokenByUid(userId) : null;

                result.add(new VehicleRecord(doc.getId(), rfidUid, userId, checkInMs, notified, fcmToken));
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

    public void pushAndSaveNotification(String uid, String title, String body) {
        if (uid == null || uid.isEmpty()) return;

        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> notiData = new HashMap<>();
        notiData.put("userId", uid);
        notiData.put("title", title);
        notiData.put("body", body);
        notiData.put("createdAt", com.google.cloud.Timestamp.now());
        notiData.put("read", false);
        db.collection("notifications").add(notiData);
        System.out.println("[FIRESTORE] Đã lưu lịch sử thông báo cho uid: " + uid);

        String fcmToken = getFcmTokenByUid(uid);
        if (fcmToken != null && !fcmToken.isEmpty()) {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("[FCM] Đã gửi Push thành công: " + response);
            } catch (FirebaseMessagingException e) {
                System.err.println("[FCM] Lỗi gửi Push notification: " + e.getMessage());
            }
        } else {
            System.out.println("[FCM] Bỏ qua gửi Push — fcmToken rỗng.");
        }
    }

    public void handleHardwareEvent(String type, String rfidUid, String userId) {
        String fullName = getFullNameByUid(userId);
        String displayName = (fullName != null && !fullName.isEmpty()) ? fullName : "Bạn";

        String title;
        String body;

        if ("IN".equalsIgnoreCase(type)) {
            title = "Xe đã vào bãi";
            body = displayName + " ơi, xe của bạn (thẻ " + rfidUid + ") vừa vào bãi đỗ.";
        } else {
            title = "Xe đã ra khỏi bãi";
            body = displayName + " ơi, xe của bạn (thẻ " + rfidUid + ") vừa rời bãi đỗ. Cảm ơn bạn!";
        }

        pushAndSaveNotification(userId, title, body);
    }
}
