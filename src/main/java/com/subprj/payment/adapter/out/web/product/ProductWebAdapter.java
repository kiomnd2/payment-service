package com.subprj.payment.adapter.out.web.product;

import com.subprj.payment.adapter.common.annotation.WebAdapter;
import com.subprj.payment.adapter.out.web.product.client.ProductClient;
import com.subprj.payment.application.port.out.LoadProductPort;
import com.subprj.payment.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
@WebAdapter
@Component
public class ProductWebAdapter implements LoadProductPort {
    private final ProductClient productClient;

    @Override
    public Flux<Product> getProducts(Long cartId, List<Long> productIds) {
        return productClient.getProducts(cartId, productIds);
    }
}
