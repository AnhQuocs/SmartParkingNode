package com.trung.payment_backend.dto;

import lombok.Data;

@Data
public class MoMoIpnRequest {
    private String partnerCode;
    private String orderId;
    private String requestId;
    private long amount;
    private String orderInfo;
    private String orderType;
    private long transId;
    private int resultCode;
    private String message;
    private String payType;
    private long responseTime;
    private String extraData;
    private String signature;
}
