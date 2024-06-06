package com.subprj.payment.adapter.out.persistent.repository;

import reactor.core.publisher.Mono;

public interface PaymentStatusUpdateRepository {

    Mono<Boolean> updatePaymentStatusToExecuting(String orderId, String paymentKey);
}
