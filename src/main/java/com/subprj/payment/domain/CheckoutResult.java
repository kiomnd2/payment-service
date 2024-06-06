package com.subprj.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class CheckoutResult {
    private Long amount;
    private String orderId;
    private String orderName;
}
