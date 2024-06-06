package com.subprj.payment.adapter.out.web.toss.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class TossPaymentExecutor {

    private final WebClient tosspaymentWebClient;
    private final String url = "/v1/payments/confirm";

    public Mono<String> execute(String paymentKey, String orderId, String amount) {
        String bodyValue = String.format("""
                {
                    "paymentKey": "%s",
                    "orderId": "%s",
                    "amount": "%s"
                }
                """, paymentKey, orderId, amount).trim();
        return tosspaymentWebClient.post()
                .uri(url)
                .bodyValue(bodyValue)
                .retrieve().bodyToMono(String.class);
    }

}
