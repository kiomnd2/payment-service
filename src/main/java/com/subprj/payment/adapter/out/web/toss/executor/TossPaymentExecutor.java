package com.subprj.payment.adapter.out.web.toss.executor;

import com.subprj.payment.domain.*;
import com.subprj.payment.adapter.out.web.toss.response.TossPaymentConfirmationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Component
public class TossPaymentExecutor implements PaymentExecutor {

    private final WebClient tosspaymentWebClient;
    private final String url = "/v1/payments/confirm";

    @Override
    public Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command) {
        String bodyValue = String.format("""
                {
                    "paymentKey": "%s",
                    "orderId": "%s",
                    "amount": "%s"
                }
                """, command.getPaymentKey(), command.getOrderId(), command.getAmount()).trim();
        return tosspaymentWebClient.post()
                .uri(url)
                .header("Idempotency-key", command.getOrderId())
                .bodyValue(bodyValue)
                .retrieve().bodyToMono(TossPaymentConfirmationResponse.class)
                .map(v -> PaymentExecutionResult.builder()
                        .paymentKey(command.getPaymentKey())
                        .orderId(command.getOrderId())
                        .extraDetails(PaymentExecutionResult.PaymentExtraDetails.builder()
                                .type(PaymentType.get(v.getType()))
                                .method(PaymentMethod.get(v.getMethod()))
                                .approvedAt(LocalDateTime.parse(v.getApprovedAt(), DateTimeFormatter.ISO_DATE_TIME))
                                .pspRawData(v.toString())
                                .orderName(v.getOrderName())
                                .pspConfirmationStatus(PSPConfirmationStatus.get(v.getStatus()))
                                .totalAmount((long) v.getTotalAmount())
                                .build())
                        .isSuccess(true)
                        .isFailure(false)
                        .isUnknown(false)
                        .isRetryable(false)
                .build());
    }
}
