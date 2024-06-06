package com.subprj.payment.adapter.out.web.product.client;

import com.subprj.payment.domain.Product;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MockProductClient implements ProductClient {
    @Override
    public Flux<Product> getProducts(Long cartId, List<Long> productIds) {
        return Flux.fromIterable(
                productIds.stream().map(
                        v -> Product.builder()
                                .id(v)
                                .sellerId(2L)
                                .amount(BigDecimal.valueOf(10000))
                                .quantity(2)
                                .name("test product")
                                .build()
                ).toList()

        );
    }
}
