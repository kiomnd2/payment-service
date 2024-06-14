package com.subprj.payment.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentConfirmResult {
    private PaymentStatus status;
    private PaymentExecutionResult.PaymentExecutionFailure failure;

    @Builder
    public PaymentConfirmResult(PaymentStatus status, PaymentExecutionResult.PaymentExecutionFailure failure) {
        if (status == PaymentStatus.FAILURE && failure == null) {
            throw new IllegalArgumentException("결제 상태 FAILURE 일 때 PaymentExecutionFailure 는 null 값이 될 수 없습니다.");
        }

        this.status = status;
        this.failure = failure;
    }

    public String getMessage() {
        switch (status) {
            case SUCCESS -> {
                return "결제 처리에 성공하였습니다";
            }
            case FAILURE -> {
                return "결제 처리에 실패하였습니다.";
            }
            case UNKNOWN -> {
                return "결제 처리 중에 알 수 없는 에러가 발생하였습니다";
            }
            default ->  {
                throw new RuntimeException(String.format("현재 결제 상태 (status: %s) 는 올바르지 않은 상태입니다.", status.name()));
            }
        }
    }
}
