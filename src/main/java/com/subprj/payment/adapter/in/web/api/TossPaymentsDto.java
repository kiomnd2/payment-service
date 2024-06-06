package com.subprj.payment.adapter.in.web.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class TossPaymentsDto {

    @Getter
    @Setter
    @ToString
    public static class TossPaymentConfirmRequest {
        private String paymentKey;
        private String orderId;
        private String amount;
    }
}
