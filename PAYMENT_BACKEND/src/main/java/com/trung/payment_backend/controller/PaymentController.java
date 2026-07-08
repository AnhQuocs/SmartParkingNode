package com.trung.payment_backend.controller;

import com.trung.payment_backend.dto.MoMoIpnRequest;
import com.trung.payment_backend.dto.PaymentRequest;
import com.trung.payment_backend.dto.PaymentResponse;
import com.trung.payment_backend.service.FirebaseService;
import com.trung.payment_backend.service.MoMoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private MoMoService moMoService;
    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/create-momo-url")
    public ResponseEntity<?> createMomoUrl(@RequestBody PaymentRequest request) {
        String payUrl = moMoService.createPaymentUrl(request.getUid(), request.getAmount());
        if (payUrl != null) {
            return ResponseEntity.ok(new PaymentResponse(payUrl));
        }
        return ResponseEntity.internalServerError().body("Không thể khởi tạo đường dẫn MoMo");
    }

    @RequestMapping(value = "/momo-ipn", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<Void> handleMoMoIpn(
            @RequestBody(required = false) MoMoIpnRequest ipnRequest,
            HttpServletRequest request) {

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return ResponseEntity.noContent().build();
        }
        System.out.println(">>> Dữ liệu MoMo gửi: " + ipnRequest.toString());
        boolean isAuthentic = moMoService.verifyIpnSignature(
                ipnRequest.getOrderId(), ipnRequest.getRequestId(), ipnRequest.getAmount(),
                ipnRequest.getOrderInfo(), ipnRequest.getOrderType(), ipnRequest.getTransId(),
                ipnRequest.getResultCode(), ipnRequest.getMessage(), ipnRequest.getPayType(),
                ipnRequest.getResponseTime(), ipnRequest.getExtraData(), ipnRequest.getSignature()
        );

        if (isAuthentic && ipnRequest.getResultCode() == 0) {
            System.out.printf("[MOMO-IPN] Giao dịch thành công! Mã đơn: %s, Số tiền: %d\n",
                    ipnRequest.getOrderId(), ipnRequest.getAmount());

            firebaseService.clearUserDebtAndLogHistory(
                    ipnRequest.getExtraData(), ipnRequest.getAmount(), ipnRequest.getTransId()
            );
            List<String> args = Arrays.asList(String.valueOf(ipnRequest.getAmount()));
            firebaseService.pushAndSaveNotification(
                    ipnRequest.getExtraData(),
                    "TITLE_PAYMENT_SUCCESS",
                    "BODY_PAYMENT_SUCCESS",
                    args
            );
        } else {
            System.err.println("[DEBUG] isAuthentic: " + isAuthentic);
            System.err.println("[DEBUG] resultCode: " + ipnRequest.getResultCode());
        }

        return ResponseEntity.noContent().build();
    }
}
