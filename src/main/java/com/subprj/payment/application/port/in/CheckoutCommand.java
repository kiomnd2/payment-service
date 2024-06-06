package com.subprj.payment.application.port.in;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
public class CheckoutCommand {
    private Long cartId;
    private List<Long> productIds ;
    private Long buyerId;
    private String idempotencyKey; // 물건에 대한 요청을 구분

}
