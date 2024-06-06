package com.subprj.payment.application.service;

import com.subprj.payment.application.port.in.CheckoutCommand;
import com.subprj.payment.application.port.in.CheckoutUseCase;
import com.subprj.payment.application.port.out.LoadProductPort;
import com.subprj.payment.application.port.out.SavePaymentPort;
import com.subprj.payment.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckoutService implements CheckoutUseCase {
    private final LoadProductPort loadProductPort;
    private final SavePaymentPort savePaymentPort;

    @Transactional
    @Override
    public Mono<CheckoutResult> checkout(CheckoutCommand command) {
        return loadProductPort.getProducts(command.getCartId(), command.getProductIds())
                .collectList()
                .map(v -> createPaymentEvent(command, v))
                .flatMap(event -> savePaymentPort.save(event).thenReturn(event))
                .map(e -> CheckoutResult.builder()
                        .orderId(e.getOrderId())
                        .amount(e.getTotalAmount())
                        .orderName(e.getOrderName())
                        .build());
    }

    private PaymentEvent createPaymentEvent(CheckoutCommand command, List<Product> products) {
        return PaymentEvent.builder()
                .buyerId(command.getBuyerId())
                .orderId(command.getIdempotencyKey())
                .orderName(products.stream()
                        .map(Product::getName)
                        .collect(Collectors.joining(",")))
                .paymentOrders(products.stream().map(v -> PaymentOrder.builder()
                        .sellerId(v.getSellerId())
                        .orderId(command.getIdempotencyKey())
                        .productId(v.getId())
                        .amount(v.getAmount().multiply(BigDecimal.valueOf(v.getQuantity())))
                        .paymentStatus(PaymentStatus.NOT_STARTED)
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
