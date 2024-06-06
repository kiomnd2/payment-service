package com.subprj.payment.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentMethod {
    EASY_PAY("간편결제");


    private final String description;

    public static PaymentMethod get(String method) {
        PaymentMethod[] values = values();
        for (PaymentMethod value : values) {
            if (method.equals(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException(method + " 은 찾을 수 없는 타입입니다");
    }
}
