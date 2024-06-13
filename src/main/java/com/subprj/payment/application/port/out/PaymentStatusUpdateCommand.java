package com.subprj.payment.application.port.out;

import com.subprj.payment.domain.PaymentExecutionResult;
import com.subprj.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public class PaymentStatusUpdateCommand {
    private String paymentKey;
    private String orderId;
    private PaymentStatus status;
    private PaymentExecutionResult.PaymentExtraDetails paymentExtraDetails;
    private PaymentExecutionResult.PaymentExecutionFailure failure;

    @Builder
    public PaymentStatusUpdateCommand(String paymentKey, String orderId
            , PaymentStatus status, PaymentExecutionResult.PaymentExtraDetails paymentExtraDetails
            , PaymentExecutionResult.PaymentExecutionFailure failure) {
        if (status != PaymentStatus.SUCCESS && status != PaymentStatus.FAILURE && status != PaymentStatus.UNKNOWN) {
            throw new IllegalArgumentException(String.format("결제 상태 (status: %s) 는 올바르지 않은 결제 상태입니다", status.name()));
        }
        if (status == PaymentStatus.SUCCESS && Objects.isNull(paymentExtraDetails)) {
            throw new IllegalArgumentException("PaymentStatus 값이 SUCCESS 라면 PaymentExtraDetails 는 null 이 되면 안됩니다.");
        } else if (status == PaymentStatus.FAILURE && Objects.isNull(failure)) {
            throw new IllegalArgumentException("PaymentStatus 값이 FAILURE 라면 PaymentExecutionFailure 는 null 이 되면 안됩니다.");
        }

        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.status = status;
        this.paymentExtraDetails = paymentExtraDetails;
        this.failure = failure;
    }
}
