package com.subprj.payment.application.port.out;

import com.subprj.payment.domain.Product;
import reactor.core.publisher.Flux;

import java.util.List;

public interface LoadProductPort {
    Flux<Product> getProducts(Long cartId, List<Long> productIds);
}
