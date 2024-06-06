package com.subprj.payment.adapter.out.persistent.repository;

import com.subprj.payment.domain.PaymentEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class R2DBCPaymentRepository implements PaymentRepository {
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Void> save(PaymentEvent paymentEvent) {
        return insertPaymentEvent(paymentEvent)
                .flatMap(v -> selectPaymentEventId())
                .flatMap(v1 -> insertPaymentOrder(paymentEvent, v1))
                .as(transactionalOperator::transactional)
                .then();
    }



    private Mono<Long> selectPaymentEventId() {
        return databaseClient.sql(Query.LAST_INSERT_ID_QUERY)
                .fetch()
                .first()
                .map(v1 -> Long.parseLong(v1.get("lastInsertId").toString()));
    }

    private Mono<Long> insertPaymentOrder(PaymentEvent paymentEvent, Long paymentEventId) {
        String valueClauses = paymentEvent.getPaymentOrders().stream().map(order -> String.format("""
                        (%d, %d, '%s', %d, %d, '%s')""".trim()
                , paymentEventId
                , order.getSellerId()
                , order.getOrderId()
                , order.getProductId()
                , order.getAmount().longValue()
                , order.getPaymentStatus()
        )).collect(Collectors.joining(","));
        return databaseClient.sql(Query.INSERT_PAYMENT_ORDER_QUERY(valueClauses))
                .fetch()
                .rowsUpdated();
    }

    private Mono<Long> insertPaymentEvent(PaymentEvent paymentEvent) {
        return databaseClient.sql(Query.INSERT_PAYMENT_EVENT_QUERY)
                .bind("buyerId", paymentEvent.getBuyerId())
                .bind("orderName", paymentEvent.getOrderName())
                .bind("orderId", paymentEvent.getOrderId())
                .fetch()
                .rowsUpdated();
    }

    @Getter
    public static class Query {
         static String INSERT_PAYMENT_EVENT_QUERY = """
                INSERT INTO payment_events (
                    buyer_id, order_name, order_id
                )values(
                    :buyerId,
                    :orderName,
                    :orderId
                );
                """.trim();
         static String LAST_INSERT_ID_QUERY = """
                 SELECT LAST_INSERT_ID() as lastInsertId
                 """.trim();

         static String INSERT_PAYMENT_ORDER_QUERY(String valueClauses) {
             return String.format("""
                     INSERT INTO payment_orders (payment_event_id, seller_id, order_id, product_id, amount, payment_order_status)
                     VALUES %s
                     """, valueClauses);
         }
    }
}
