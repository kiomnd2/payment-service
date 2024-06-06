package com.subprj.payment.application.port.out;

import com.subprj.payment.domain.PaymentEvent;
import reactor.core.publisher.Mono;

public interface SavePaymentPort {
    Mono<Void> save(PaymentEvent event);
}
