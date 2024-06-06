package com.subprj.payment.application.port.out;

import reactor.core.publisher.Mono;

public interface PaymentStatusUpdatePort {

    Mono<Boolean> updatePaymentStatusToExecuting(String orderId, String paymentKey);
}
