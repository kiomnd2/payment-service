package com.subprj.payment.adapter.out.persistent.repository;

import com.subprj.payment.domain.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentRepository {
    Mono<Void> save(PaymentEvent paymentEvent);
}
