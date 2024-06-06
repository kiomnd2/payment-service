package com.subprj.payment.application.test;

import com.subprj.payment.domain.PaymentEvent;
import reactor.core.publisher.Mono;

public interface PaymentDatabaseHelper {

    PaymentEvent getPayments(String orderId);

    Mono<Void> clear();
}
