package com.subprj.payment.adapter.out.web.toss.executor;

import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentExecutionResult;
import reactor.core.publisher.Mono;

public interface PaymentExecutor {
    Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command);
}
