package com.subprj.payment.application.test;

import com.subprj.payment.domain.PaymentEvent;
import com.subprj.payment.domain.PaymentOrder;
import com.subprj.payment.domain.PaymentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class R2DBCPaymentDatabaseHelper implements PaymentDatabaseHelper {
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;

    public R2DBCPaymentDatabaseHelper(DatabaseClient databaseClient, TransactionalOperator transactionalOperator) {
        this.databaseClient = databaseClient;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public PaymentEvent getPayments(String orderId) {
        return databaseClient.sql(Query.SELECT_PAYMENT_QUERY)
                .bind("orderId", orderId)
                .fetch()
                .all()
                .groupBy(stringObjectMap -> Long.parseLong(stringObjectMap.get("payment_event_id").toString()))
                .flatMap( longMapGroupedFlux -> longMapGroupedFlux.collectList().map(results -> PaymentEvent.builder()
                        .id(longMapGroupedFlux.key())
                        .orderId(results.stream().map(v-> v.get("order_id")).findFirst()
                                .orElseThrow(() -> new RuntimeException("찾을 수 없음")).toString())
                        .orderName(results.stream().map(v-> v.get("order_name")).findFirst()
                                .orElseThrow(() -> new RuntimeException("찾을 수 없음")).toString())
                        .buyerId(Long.parseLong(results.stream().map(o -> Long.parseLong(Objects.toString(o.get("buyer_id")))).findFirst()
                                .orElseThrow(() -> new RuntimeException("찾을 수 없음")).toString()))
                        .isPaymentDone(results.stream().map(v -> v.get("is_payment_done")).findFirst()
                                .orElseThrow(() -> new RuntimeException("찾을 수 없음")).toString()
                                .equals("1"))
                        .paymentOrders(results.stream().map( result -> PaymentOrder.builder()
                                .id(Long.parseLong(result.get("id").toString()))
                                .paymentEventId(longMapGroupedFlux.key())
                                .sellerId(Long.parseLong(result.get("seller_id").toString()))
                                .buyerId(Long.parseLong(result.get("buyer_id").toString()))
                                .orderId(result.get("order_id").toString())
                                .productId(Long.parseLong(result.get("product_id").toString()))
                                .amount(new BigDecimal((result.get("amount").toString())))
                                .paymentStatus(
                                        PaymentStatus.get(result.get("payment_order_status").toString()))
                                .isLedgerUpdated(result.get("ledger_updated").toString().equals("1"))
                                .isWalletUpdated(result.get("wallet_updated").toString().equals("1"))
                                .build()
                        ).collect(Collectors.toList()))
                        .build()))
                .blockFirst();
    }

    @Override
    public Mono<Void> clear() {
        return deletePaymentOrder()
                .flatMap(v -> deletePaymentOrder())
                .as(transactionalOperator::transactional)
                .then();
    }

    private Mono<Long> deletePaymentOrder() {
        return databaseClient.sql(Query.DELETE_PAYMENT_ORDER_QUERY).fetch().rowsUpdated();
    }

    private Mono<Long> deletePaymentEvent() {
        return databaseClient.sql(Query.DELETE_PAYMENT_EVENT_QUERY).fetch().rowsUpdated();
    }


    public static class Query {
        static String SELECT_PAYMENT_QUERY = """
        SELECT * FROM payment_events pe
        INNER JOIN payment_orders po on pe.order_id = po.order_id
        WHERE pe.order_id = :orderId
        """.trim();

        static String DELETE_PAYMENT_EVENT_QUERY = """
                DELETE FROM payment_events
                """.trim();

        static String DELETE_PAYMENT_ORDER_QUERY = """
        DELETE FROM payment_orders
        """.trim();
    }
}
