package com.subprj.payment.adapter.out.persistent.exception;

import lombok.Getter;

@Getter
public class PaymentValidException extends RuntimeException {
    private String message;

    public PaymentValidException(String message) {
        this.message = message;
    }
}
