package com.subprj.payment.adapter.in.web.view;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

public class CheckoutDto {

    @Getter
    @Setter
    @ToString
    public static class CheckoutRequest {
        private Long cartId = 1L;
        private List<Long> productIds = List.of(1L,2L,3L) ;
        private Long buyerId = 1L;
        private String seed = LocalDateTime.now().toString(); // 물건에 대한 요청을 구분
    }
}
