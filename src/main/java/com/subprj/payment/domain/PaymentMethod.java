package com.subprj.payment.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentMethod {
    EASY_PAY("간편결제");


    private final String description;
}
