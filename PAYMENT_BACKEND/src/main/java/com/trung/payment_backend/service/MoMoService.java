package com.trung.payment_backend.service;

import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MoMoService {

    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${momo.endpoint}")
    private String endpoint;
    @Value("${momo.partner-code}")
    private String partnerCode;
    @Value("${momo.access-key}")
    private String accessKey;
    @Value("${momo.secret-key}")
    private String secretKey;
    @Value("${momo.redirect-url}")
    private String redirectUrl;
    @Value("${momo.ipn-url}")
    private String ipnUrl;

    public String createPaymentUrl(String uid, long amount) {
        String orderId = uid + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toan du no bai do xe";
        String requestType = "captureWallet";
        String extraData = uid;

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = new HmacUtils("HmacSHA256", secretKey).hmacHex(rawSignature);

        Map<String, Object> payload = new HashMap<>();
        payload.put("partnerCode", partnerCode);
        payload.put("accessKey", accessKey);
        payload.put("requestId", requestId);
        payload.put("amount", amount);
        payload.put("orderId", orderId);
        payload.put("orderInfo", orderInfo);
        payload.put("redirectUrl", redirectUrl);
        payload.put("ipnUrl", ipnUrl);
        payload.put("extraData", extraData);
        payload.put("requestType", requestType);
        payload.put("signature", signature);
        payload.put("lang", "vi");

        try {
            System.out.println(">>> IPN URL: " + ipnUrl);
            System.out.println("Order ID: " + orderId);

            Map<String, Object> response = restTemplate.postForObject(endpoint, payload, Map.class);

            if (response != null && response.containsKey("payUrl")) {
                return (String) response.get("payUrl");
            }
        } catch (Exception e) {
            System.err.println("[MOMO] Lỗi kết nối cổng thanh toán: " + e.getMessage());
        }
        return null;
    }

    public boolean verifyIpnSignature(String orderId, String requestId, long amount, String orderInfo,
                                      String orderType, long transId, int resultCode, String message,
                                      String payType, long responseTime, String extraData, String incomingSignature) {

        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&message=" + message +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&partnerCode=" + partnerCode +
                "&payType=" + payType +
                "&requestId=" + requestId +
                "&responseTime=" + responseTime +
                "&resultCode=" + resultCode +
                "&transId=" + transId;

        String calculatedSignature = new HmacUtils("HmacSHA256", secretKey).hmacHex(rawSignature);

        System.out.println("Signature MoMo gửi: " + incomingSignature);
        System.out.println("Signature tự tính: " + calculatedSignature);

        return calculatedSignature.equals(incomingSignature);
    }
}
