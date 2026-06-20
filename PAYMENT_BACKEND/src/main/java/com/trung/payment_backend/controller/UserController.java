package com.trung.payment_backend.controller;

import com.trung.payment_backend.dto.FcmTokenRequest;
import com.trung.payment_backend.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/fcm-token")
    public ResponseEntity<String> registerFcmToken(@RequestBody FcmTokenRequest request) {
        if (request == null || request.getUid() == null || request.getFcmToken() == null
                || request.getUid().isEmpty() || request.getFcmToken().isEmpty()) {
            return ResponseEntity.badRequest().body("Thiếu uid hoặc fcmToken");
        }

        boolean ok = firebaseService.saveFcmToken(request.getUid(), request.getFcmToken());
        if (ok) {
            return ResponseEntity.ok("Đã lưu fcmToken");
        }
        return ResponseEntity.internalServerError().body("Không tìm thấy user hoặc lỗi lưu token");
    }
}