package com.subprj.payment.adapter.out.persistent;

import com.subprj.payment.adapter.common.annotation.PersistentAdapter;
import com.subprj.payment.adapter.out.persistent.repository.PaymentRepository;
import com.subprj.payment.application.port.out.SavePaymentPort;
import com.subprj.payment.domain.PaymentEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@PersistentAdapter
public class PaymentPersistentAdapter implements SavePaymentPort {
    private final PaymentRepository paymentRepository;

    public Mono<Void> save(PaymentEvent paymentEvent) {
        return paymentRepository.save(paymentEvent);
    }

}
