package com.subprj.payment.adapter.out.persistent.exception;

import com.subprj.payment.domain.PaymentStatus;

public class PaymentAlreadyProcessedException extends RuntimeException{
    private PaymentStatus status;
    private String message;

    public PaymentAlreadyProcessedException(PaymentStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
