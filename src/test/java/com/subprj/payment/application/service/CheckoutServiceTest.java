package com.subprj.payment.application.service;

import com.subprj.payment.application.port.in.CheckoutCommand;
import com.subprj.payment.application.port.in.CheckoutUseCase;
import com.subprj.payment.application.test.PaymentDatabaseHelper;
import com.subprj.payment.application.test.PaymentTestConfiguration;
import com.subprj.payment.domain.CheckoutResult;
import com.subprj.payment.domain.PaymentEvent;
import com.subprj.payment.domain.PaymentOrder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Import(PaymentTestConfiguration.class)
@SpringBootTest
public class CheckoutServiceTest {

    @Autowired
    CheckoutUseCase checkoutUseCase;
    @Autowired
    PaymentDatabaseHelper paymentDatabaseHelper;

    @BeforeEach
    void setup() {
        paymentDatabaseHelper.clear().block();
    }

    @Test
    void savePaymentAndPaymentOrder_success() {
        String orderId= UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();

        StepVerifier.create(checkoutUseCase.checkout(command))
                .expectNextMatches(checkoutResult
                        -> {
                    System.out.println("checkoutResult = " + checkoutResult.getAmount());
                    System.out.println("checkoutResult = " + checkoutResult.getOrderId());
                    return checkoutResult.getAmount() == 60000L && checkoutResult.getOrderId().equals(orderId);
                }).verifyComplete();

        PaymentEvent paymentEvent = paymentDatabaseHelper.getPayments(orderId);

        assertThat(paymentEvent.getOrderId()).isEqualTo(orderId);
        assertThat(paymentEvent.getPaymentOrders().size()).isEqualTo(command.getProductIds().size());
        assertThat(paymentEvent.getIsPaymentDone()).isFalse();
        assertThat(paymentEvent.getPaymentOrders().stream().allMatch(PaymentOrder::getIsLedgerUpdated)).isFalse();
        assertThat(paymentEvent.getPaymentOrders().stream().allMatch(PaymentOrder::getIsWalletUpdated)).isFalse();
    }

    @Test
    void failToSavePaymentEventAndPaymentOrderWhenTryTwice_fail() {
        String orderId= UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();

        checkoutUseCase.checkout(command).block();
        org.junit.jupiter.api.Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            checkoutUseCase.checkout(command).block();
        });
    }

}
