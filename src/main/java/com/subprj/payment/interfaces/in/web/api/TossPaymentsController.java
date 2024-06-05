package com.subprj.payment.interfaces.in.web.api;

import com.subprj.payment.interfaces.in.web.common.annotation.WebAdapter;
import com.subprj.payment.interfaces.in.web.common.response.CommonResponse;
import com.subprj.payment.interfaces.out.web.executor.TossPaymentExecutor;
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
    private final TossPaymentExecutor tossPaymentExecutor;

    @PostMapping("/confirm")
    public Mono<ResponseEntity<CommonResponse<String>>> confirm(@RequestBody TossPaymentsDto.TossPaymentConfirmRequest request) {
        return tossPaymentExecutor.execute(
                request.getPaymentKey(), request.getOrderId(), request.getAmount()
        ).map(v -> ResponseEntity.ok().body(
                CommonResponse.with(HttpStatus.OK, "ok", v)
        ));
    }
}
