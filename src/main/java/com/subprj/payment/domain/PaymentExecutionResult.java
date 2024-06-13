package com.subprj.payment.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Getter
public class PaymentExecutionResult {
    private String paymentKey;
    private String orderId;
    private PaymentExtraDetails extraDetails;
    private PaymentExecutionFailure failure;
    private Boolean isSuccess;
    private Boolean isFailure;
    private Boolean isUnknown;
    private Boolean isRetryable;

    @Builder
    public PaymentExecutionResult(String paymentKey, String orderId, PaymentExtraDetails extraDetails, PaymentExecutionFailure failure
            , Boolean isSuccess, Boolean isFailure, Boolean isUnknown, Boolean isRetryable) {
        if (Stream.of(isSuccess, isFailure, isUnknown).filter(v-> v).count() != 1) { // 3 가지 경우의 수중 하나만 true 여야함
            throw new RuntimeException(String.format("결제 (orderId: %s)는 올바르지 않은 결제 상태입니다. sms", orderId));
        }


        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.extraDetails = extraDetails;
        this.failure = failure;
        this.isSuccess = isSuccess;
        this.isFailure = isFailure;
        this.isUnknown = isUnknown;
        this.isRetryable = isRetryable;
    }

    public PaymentStatus getPaymentStatus() {
        if (isSuccess) return PaymentStatus.SUCCESS;
        else if (isFailure) return PaymentStatus.FAILURE;
        else if (isUnknown) return PaymentStatus.UNKNOWN;
        else {
            throw new IllegalArgumentException(String.format("결제 (orderId : %s) 는 올바르지 않은 결제 상태입니다.", orderId));
        }
    }

    public static class PaymentExtraDetails {
        private PaymentType type;
        private PaymentMethod method;
        private LocalDateTime approvedAt;
        private String orderName;
        private PSPConfirmationStatus pspConfirmationStatus;
        private Long totalAmount;
        private String pspRawData ;


        @Builder
        public PaymentExtraDetails(PaymentType type, PaymentMethod method, LocalDateTime approvedAt, String orderName, PSPConfirmationStatus pspConfirmationStatus
                , Long totalAmount, String pspRawData) {
            this.type = type;
            this.method = method;
            this.approvedAt = approvedAt;
            this.orderName = orderName;
            this.pspConfirmationStatus = pspConfirmationStatus;
            this.totalAmount = totalAmount;
            this.pspRawData = pspRawData;
        }
    }

    public static class PaymentExecutionFailure {
        private String errorCode;
        private String message;
    }
}
