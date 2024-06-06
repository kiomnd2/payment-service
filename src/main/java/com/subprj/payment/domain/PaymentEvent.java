package com.subprj.payment.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Builder
public class PaymentEvent {
    private Long id;
    private Long buyerId;
    private String orderName;
    private String orderId;
    private String paymentKey;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private LocalDateTime approvedAt;
    private List<PaymentOrder> paymentOrders;
    private Boolean isPaymentDone;

    public Long getTotalAmount() {
        return paymentOrders.stream().mapToLong(v->v.getAmount().longValue()).sum();
    }
}
