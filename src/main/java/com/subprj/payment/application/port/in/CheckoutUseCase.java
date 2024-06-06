package com.subprj.payment.application.port.in;

import com.subprj.payment.domain.CheckoutResult;
import reactor.core.publisher.Mono;

public interface CheckoutUseCase {

    Mono<CheckoutResult> checkout(CheckoutCommand command);
}
