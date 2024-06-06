package com.subprj.payment.domain;

import com.subprj.payment.adapter.out.web.toss.response.TossPaymentConfirmationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentType {
    NORMAL("일반");

    private final String description;

    public static PaymentType get(String type) {
        PaymentType[] values = values();
        for (PaymentType value : values) {
            if (type.equals(value.name())) {
                return value;
            }
        }
        throw new IllegalArgumentException(type + " 은 찾을 수 없는 타입입니다");
    }
}
