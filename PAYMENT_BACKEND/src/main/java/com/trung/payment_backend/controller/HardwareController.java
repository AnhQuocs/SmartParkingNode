package com.trung.payment_backend.controller;

import com.trung.payment_backend.dto.HardwareEventRequest;
import com.trung.payment_backend.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        } else {
            System.err.println("[HARDWARE] Thiếu userId — bỏ qua gửi FCM cho sự kiện này");
        }

        return ResponseEntity.ok("Received");
    }
}