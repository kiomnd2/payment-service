package com.subprj.payment.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Product {
    private Long id;
    private BigDecimal amount;
    private Integer quantity;
    private String name;
    private Long sellerId;
}
