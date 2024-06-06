package com.subprj.payment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@ToString
@Getter
@Builder
public class PaymentOrder {
    private Long id;
    private Long paymentEventId;
    private Long sellerId;
    private Long buyerId;
    private Long productId;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private Boolean isLedgerUpdated;
    private Boolean isWalletUpdated;
}
