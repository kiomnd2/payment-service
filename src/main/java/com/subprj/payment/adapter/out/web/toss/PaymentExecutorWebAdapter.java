package com.subprj.payment.adapter.out.web.toss;

import com.subprj.payment.adapter.common.annotation.WebAdapter;
import com.subprj.payment.adapter.out.web.toss.executor.PaymentExecutor;
import com.subprj.payment.application.port.out.PaymentExecutorPort;
import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentExecutionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@WebAdapter
@Component
public class PaymentExecutorWebAdapter implements PaymentExecutorPort {
    private final PaymentExecutor paymentExecutor;

    @Override
    public Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command) {
        return paymentExecutor.execute(command);
    }
}
