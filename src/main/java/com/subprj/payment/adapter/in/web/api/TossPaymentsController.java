package com.subprj.payment.adapter.in.web.api;

import com.subprj.payment.adapter.common.annotation.WebAdapter;
import com.subprj.payment.adapter.common.response.CommonResponse;
import com.subprj.payment.adapter.out.web.toss.executor.TossPaymentExecutor;
import com.subprj.payment.application.port.in.PaymentConfirmUseCase;
import com.subprj.payment.domain.PaymentConfirmCommand;
import com.subprj.payment.domain.PaymentConfirmResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@WebAdapter
@RequestMapping("/v1/toss")
@RestController
public class TossPaymentsController {
    private final PaymentConfirmUseCase paymentConfirmUseCase;

    @PostMapping("/confirm")
    public Mono<ResponseEntity<CommonResponse<PaymentConfirmResult>>> confirm(@RequestBody TossPaymentsDto.TossPaymentConfirmRequest request) {
        PaymentConfirmCommand command = PaymentConfirmCommand.builder()
                .paymentKey(request.getPaymentKey())
                .orderId(request.getOrderId())
                .amount(Long.parseLong(request.getAmount()))
                .build();

        return paymentConfirmUseCase.confirm(command)
                .map(m -> ResponseEntity.ok().body(CommonResponse.with(HttpStatus.OK, "", m)));
    }
}
