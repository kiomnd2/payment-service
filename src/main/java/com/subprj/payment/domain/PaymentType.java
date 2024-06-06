package com.subprj.payment.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentType {
    NORMAL("일반");

    private final String description;
}
