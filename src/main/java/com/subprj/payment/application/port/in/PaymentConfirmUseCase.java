package com.subprj.payment.application.port.in;

import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentConfirmResult;
import reactor.core.publisher.Mono;

public interface PaymentConfirmUseCase {
    Mono<PaymentConfirmResult> confirm(PaymentConfirmCommand paymentConfirmCommand);
}
