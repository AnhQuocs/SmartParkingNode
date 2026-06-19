package com.trung.payment_backend.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
}
