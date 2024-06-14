package com.subprj.payment.application.service;

import com.subprj.payment.application.port.in.CheckoutCommand;
import com.subprj.payment.application.port.in.CheckoutUseCase;
import com.subprj.payment.application.port.out.PaymentExecutorPort;
import com.subprj.payment.application.port.out.PaymentStatusUpdatePort;
import com.subprj.payment.application.port.out.PaymentValidationPort;
import com.subprj.payment.application.test.PaymentDatabaseHelper;
import com.subprj.payment.application.test.PaymentTestConfiguration;
import com.subprj.payment.domain.*;
import io.mockk.impl.annotations.MockK;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(PaymentTestConfiguration.class)
class PaymentConfirmServiceTest {
    @Autowired
    private CheckoutUseCase checkoutUseCase;

    @Autowired
    PaymentStatusUpdatePort paymentStatusUpdatePort;

    @Autowired
    PaymentValidationPort paymentValidationPort;

    @Autowired
    PaymentDatabaseHelper paymentDatabaseHelper;


    @BeforeEach
    void setup() {
        paymentDatabaseHelper.clear().block();
    }

    @Test
    void shouldBeMarkedAsSuccessIfPaymentConfirmationSuccessInPsp() {
        String orderId = UUID.randomUUID().toString();
        CheckoutCommand command = CheckoutCommand.builder()
                .cartId(1L)
                .buyerId(1L)
                .productIds(List.of(1L, 2L, 3L))
                .idempotencyKey(orderId)
                .build();

        CheckoutResult checkoutResult = checkoutUseCase.checkout(command).block();

        PaymentExecutorPort mock = mock(PaymentExecutorPort.class);
        PaymentConfirmCommand paymentConfirmCommand = PaymentConfirmCommand.builder()
                .paymentKey(UUID.randomUUID().toString())
                .orderId(orderId)
                .amount(checkoutResult.getAmount())
                .build();

        PaymentConfirmService paymentConfirmService = new PaymentConfirmService(paymentStatusUpdatePort, paymentValidationPort, mock);

        PaymentExecutionResult paymentConfirmResult = PaymentExecutionResult.builder()
                .paymentKey(paymentConfirmCommand.getPaymentKey())
                .orderId(paymentConfirmCommand.getOrderId())
                .extraDetails(PaymentExecutionResult.PaymentExtraDetails.builder()
                        .type(PaymentType.NORMAL)
                        .method(PaymentMethod.EASY_PAY)
                        .totalAmount(paymentConfirmCommand.getAmount())
                        .orderName("test_ordername")
                        .pspConfirmationStatus(PSPConfirmationStatus.DONE)
                        .approvedAt(LocalDateTime.now())
                        .pspRawData("{}")
                        .build())
                .isSuccess(true)
                .isFailure(false)
                .isUnknown(false)
                .isRetryable(false)
                .build();

        when(mock.execute(paymentConfirmCommand)).thenReturn(Mono.just(paymentConfirmResult));

        PaymentConfirmResult block = paymentConfirmService.confirm(paymentConfirmCommand).block();

        Assertions.assertThat(block.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}