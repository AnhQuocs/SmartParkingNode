package com.trung.payment_backend.controller;
import com.trung.payment_backend.dto.MoMoIpnRequest;
import com.trung.payment_backend.dto.PaymentRequest;
import com.trung.payment_backend.dto.PaymentResponse;
import com.trung.payment_backend.service.FirebaseService;
import com.trung.payment_backend.service.MoMoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired private MoMoService moMoService;
    @Autowired private FirebaseService firebaseService;

    @PostMapping("/create-momo-url")
    public ResponseEntity<?> createMomoUrl(@RequestBody PaymentRequest request) {
        String payUrl = moMoService.createPaymentUrl(request.getUid(), request.getAmount());
        if (payUrl != null) {
            return ResponseEntity.ok(new PaymentResponse(payUrl));
        }
        return ResponseEntity.internalServerError().body("Không thể khởi tạo đường dẫn MoMo");
    }

//    @PostMapping("/momo-ipn")
//    public ResponseEntity<Void> handleMoMoIpn(@RequestBody MoMoIpnRequest ipnRequest) {
//
//        boolean isAuthentic = moMoService.verifyIpnSignature(
//                ipnRequest.getOrderId(), ipnRequest.getRequestId(), ipnRequest.getAmount(),
//                ipnRequest.getOrderInfo(), ipnRequest.getOrderType(), ipnRequest.getTransId(),
//                ipnRequest.getResultCode(), ipnRequest.getMessage(), ipnRequest.getPayType(),
//                ipnRequest.getResponseTime(), ipnRequest.getExtraData(), ipnRequest.getSignature()
//        );
//
//        if (isAuthentic && ipnRequest.getResultCode() == 0) {
//            System.out.printf("[MOMO-IPN] Giao dịch thành công! Mã đơn: %s, Số tiền: %d\n",
//                    ipnRequest.getOrderId(), ipnRequest.getAmount());
//
//            firebaseService.clearUserDebtAndLogHistory(
//                    ipnRequest.getExtraData(), ipnRequest.getAmount(), ipnRequest.getTransId()
//            );
//        } else {
//            System.err.println("[MOMO-IPN] Giao dịch thất bại hoặc chuỗi Signature không trùng khớp!");
//        }
//
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/momo-ipn")
    public ResponseEntity<Void> handleMoMoIpn(@RequestBody String rawPayload) {
        // Hứng mọi thứ thành chuỗi text thuần túy để xem MoMo có thực sự gọi đến không
        System.out.println("=========================================");
        System.out.println(">>> [CẢNH BÁO] ĐÃ CÓ REQUEST GỌI VÀO CỔNG IPN!");
        System.out.println(">>> Dữ liệu thô MoMo gửi về: ");
        System.out.println(rawPayload);
        System.out.println("=========================================");

        // Tạm thời trả về 204 để MoMo ngừng gửi lại
        return ResponseEntity.noContent().build();
    }
}
