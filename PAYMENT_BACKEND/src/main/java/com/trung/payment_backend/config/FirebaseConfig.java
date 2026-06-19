package com.trung.payment_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-path}")
    private Resource configFile;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(configFile.getInputStream()))
                        .build();
                FirebaseApp.initializeApp(options);
                System.out.println("[FIREBASE] Khởi tạo Firebase Admin SDK thành công!");
            }
        } catch (IOException e) {
            System.err.println("[FIREBASE] Lỗi đọc file cấu hình: " + e.getMessage());
        }
    }
}