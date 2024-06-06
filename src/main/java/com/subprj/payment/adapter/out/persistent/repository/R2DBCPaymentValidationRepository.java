package com.subprj.payment.adapter.out.persistent.repository;

import com.subprj.payment.adapter.out.persistent.exception.PaymentValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;

@RequiredArgsConstructor
@Repository
public class R2DBCPaymentValidationRepository implements PaymentValidationRepository {
    private final DatabaseClient databaseClient;

    @Override
    public Mono<Boolean> isValid(String orderId, Long amount) {
        return databaseClient.sql(Query.SELECT_PAYMENT_TOTAL_AMOUNT_QUERY)
                .bind("orderId", orderId)
                .fetch()
                .first()
                .handle((row, sink) -> {
                    BigDecimal totalAmount = new BigDecimal(row.get("total_amount").toString());
                    if (totalAmount.compareTo(BigDecimal.valueOf(amount)) == 0) {
                        sink.next(true);
                    } else {
                        sink.error(new PaymentValidException(
                                String.format("결제 (orderId : %s) 에서 금액 (amount: %d) 이 올바르지 않습니다"
                                        , orderId, amount)));
                    }
                })
                ;
    }

    public static class Query {
        static String SELECT_PAYMENT_TOTAL_AMOUNT_QUERY = """
        SELECT SUM(amount) as total_amount
        FROM payment_orders
        WHERE order_id = :orderId
        """;
    }
}
