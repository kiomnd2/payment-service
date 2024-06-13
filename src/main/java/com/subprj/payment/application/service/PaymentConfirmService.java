package com.subprj.payment.application.service;

import com.subprj.payment.adapter.common.annotation.UseCase;
import com.subprj.payment.application.port.in.PaymentConfirmUseCase;
import com.subprj.payment.application.port.out.PaymentExecutorPort;
import com.subprj.payment.application.port.out.PaymentStatusUpdateCommand;
import com.subprj.payment.application.port.out.PaymentStatusUpdatePort;
import com.subprj.payment.application.port.out.PaymentValidationPort;
import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentConfirmResult;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@UseCase
public class PaymentConfirmService implements PaymentConfirmUseCase {
    private final PaymentStatusUpdatePort paymentStatusUpdatePort;
    private final PaymentValidationPort paymentValidationPort;
    private final PaymentExecutorPort paymentExecutorPort;

    @Override
    public Mono<PaymentConfirmResult> confirm(PaymentConfirmCommand paymentConfirmCommand) {
        return paymentStatusUpdatePort.updatePaymentStatusToExecuting(paymentConfirmCommand.getOrderId()
                , paymentConfirmCommand.getPaymentKey())
                .filterWhen(v -> paymentValidationPort.isValid(paymentConfirmCommand.getOrderId()
                        , paymentConfirmCommand.getAmount()))
                .flatMap(v -> paymentExecutorPort.execute(paymentConfirmCommand))
                .flatMap(v -> paymentStatusUpdatePort.updatePaymentStatus(
                        PaymentStatusUpdateCommand.builder()
                                .paymentKey(v.getPaymentKey())
                                .orderId(v.getOrderId())
                                .status(v.getPaymentStatus())
                                .paymentExtraDetails(v.getExtraDetails())
                                .failure(v.getFailure())
                                .build()
                )).map(v -> new PaymentConfirmResult())
        ;
    }
}
