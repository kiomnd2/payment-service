package com.subprj.payment.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentStatus {
    NOT_STARTED("결제 시작전"),
    EXECUTING("결제 승인 중"),
    SUCCESS("결제 승인 완료"),
    FAILURE("결제 승인 실패"),
    UNKNOWN("결제 승인 알 수 없는 상태");

    private final String description;

    public static PaymentStatus get(String status) {
        PaymentStatus[] values = values();
        for (PaymentStatus value : values) {
            if (value.name().equals(status)) {
                return value;
            }
        }
        throw new IllegalArgumentException("찾을 수 없는 타입");
    }
}
