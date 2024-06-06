package com.subprj.payment.application.port.out;

import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentExecutionResult;
import reactor.core.publisher.Mono;

public interface PaymentExecutorPort {
    Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command);
}
