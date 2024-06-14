package com.subprj.payment.adapter.out.persistent.repository;

import com.subprj.payment.adapter.out.persistent.exception.PaymentAlreadyProcessedException;
import com.subprj.payment.application.port.out.PaymentStatusUpdateCommand;
import com.subprj.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class R2DBCPaymentStatusUpdateRepository implements PaymentStatusUpdateRepository {
    private final DatabaseClient databaseClient;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<Boolean> updatePaymentStatusToExecuting(String orderId, String paymentKey) {
        return checkPreviousPaymentOrderStatus(orderId)
                .flatMap(m -> insertPaymentHistory(m, PaymentStatus.EXECUTING, "PAYMENT_CONFIRMATION_START"))
                .flatMap(m -> updatePaymentOrderStatus(orderId, PaymentStatus.EXECUTING))
                .flatMap(m -> updatePaymentKey(orderId, paymentKey))
                .as(transactionalOperator::transactional)
                .thenReturn(true);
    }

    private Mono<Boolean> updatePaymentStatusToSuccess(PaymentStatusUpdateCommand command) {
        return selectPaymentOrderStatus(command.getOrderId())
                .collectList()
                .flatMap( v -> insertPaymentHistory(v, command.getStatus(), "PAYMENT_CONFIRMATION_DONE"))
                .flatMap( v-> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
                .flatMap( v-> updatePaymentEventExtraDetails(command))
                .as(transactionalOperator::transactional)
                .thenReturn(true);
    }

    private Mono<Boolean> updatePaymentStatusToFailure(PaymentStatusUpdateCommand command) {
        return selectPaymentOrderStatus(command.getOrderId())
                .collectList()
                .flatMap(v -> insertPaymentHistory(v, command.getStatus(), command.getFailure().toString()))
                .flatMap(v -> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
                .as(transactionalOperator::transactional)
                .thenReturn(true);
    }


    private Mono<Boolean> updatePaymentStatusToUnknown(PaymentStatusUpdateCommand command) {
        return selectPaymentOrderStatus(command.getOrderId())
                .collectList()
                .flatMap(v -> insertPaymentHistory(v, command.getStatus(), "UNKNOWN"))
                .flatMap(v -> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
                .flatMap(v-> increamentPaymentOrderFailedCount(command))
                .as(transactionalOperator::transactional)
                .thenReturn(true);
    }

    private Mono<Long> increamentPaymentOrderFailedCount(PaymentStatusUpdateCommand command) {
        return databaseClient.sql(Query.INCREMENT_PAYMENT_ORDER_FAILED_COUNT_QUERY)
                .bind("orderId", command.getOrderId())
                .fetch()
                .rowsUpdated();
    }

    private Mono<Long> updatePaymentEventExtraDetails(PaymentStatusUpdateCommand command) {
        return databaseClient.sql(Query.UPDATE_PAYMENT_EVENT_EXTRA_DETAILS_QUERY)
                .bind("orderName", command.getPaymentExtraDetails().getOrderName())
                .bind("method", command.getPaymentExtraDetails().getMethod().name())
                .bind("approvedAt", command.getPaymentExtraDetails().getApprovedAt().toString())
                .bind("orderId" , command.getOrderId())
                .bind("type", command.getPaymentExtraDetails().getType())
                .fetch()
                .rowsUpdated();
    }


    @Override
    public Mono<Boolean> updatePaymentStatus(PaymentStatusUpdateCommand command) {
        switch (command.getStatus()) {
            case SUCCESS -> {
                return updatePaymentStatusToSuccess(command);
            }
            case FAILURE -> {
                return updatePaymentStatusToFailure(command);
            }
            case UNKNOWN -> {
                return updatePaymentStatusToUnknown(command);
            }
            default ->
                    throw new IllegalArgumentException(String.format("결제 상태 (status: %s 는 올바르지 않은 결제 상태입니다", command.getStatus().name()));
        }
    }

    private Mono<Long> updatePaymentOrderStatus(String orderId, PaymentStatus paymentStatus) {
        return databaseClient.sql(Query.UPDATE_PAYMENT_ORDER_STATUS)
                .bind("orderId", orderId)
                .bind("status", paymentStatus.name())
                .fetch()
                .rowsUpdated();
    }

    private Mono<Long> insertPaymentHistory(List<Pair<Long, PaymentStatus>> paymentOrderIdStatus
            , PaymentStatus paymentStatus
            , String reason) {
        if (paymentOrderIdStatus.isEmpty()) return Mono.empty();

        String valuesClauses = paymentOrderIdStatus.stream()
                .map(v -> String.format("(%d, '%s', '%s', '%s')",
                        v.getFirst(),
                        v.getSecond().name(),
                        paymentStatus,
                        reason
                )).collect(Collectors.joining(", "));
        return databaseClient.sql(Query.INSERT_PAYMENT_HISTORY_QUERY(valuesClauses))
                .fetch()
                .rowsUpdated();
    }

    private Flux<Pair<Long, PaymentStatus>> selectPaymentOrderStatus(String orderId) {
        return databaseClient.sql(Query.SELECT_PAYMENT_ORDER_STATUS_QUERY)
                .bind("orderId", orderId)
                .fetch()
                .all()
                .map(v -> Pair.of(Long.parseLong(v.get("id").toString()), PaymentStatus.get(v.get("payment_order_status").toString())));
    }

    private Mono<List<Pair<Long, PaymentStatus>>> checkPreviousPaymentOrderStatus(String orderId) {
        return selectPaymentOrderStatus(orderId)
                .handle((paymentOrder, sink) -> {
                    switch (paymentOrder.getSecond()) {
                        case NOT_STARTED, EXECUTING, UNKNOWN: {
                            sink.next(paymentOrder);
                            break;
                        }
                        case SUCCESS: {
                            sink.error(new PaymentAlreadyProcessedException(PaymentStatus.SUCCESS, "이미 처리된 결제 입니다.")); break;
                        }
                        case FAILURE: {
                            sink.error(new PaymentAlreadyProcessedException(PaymentStatus.FAILURE, "이미 에러 처리된 결제 입니다.")); break;
                        }
                    }
                }).map(m -> (Pair<Long, PaymentStatus>) m).collectList();
    }

    private Mono<Long> updatePaymentKey(String orderId, String paymentKey) {
        return databaseClient.sql(Query.UPDATE_PAYMENT_KEY_QUERY)
                .bind("orderId", orderId)
                .bind("paymentKey", paymentKey)
                .fetch()
                .rowsUpdated();
    }

    public static class Query {
        static String SELECT_PAYMENT_ORDER_STATUS_QUERY = """
                SELECT id, payment_order_status
                FROM payment_orders
                WHERE order_id = :orderId
                """.trim();

        static String INSERT_PAYMENT_HISTORY_QUERY(String valueClauses) {
            return String.format("""
                    INSERT INTO payment_order_histories(payment_order_id, previous_status, new_status, reason)
                    VALUES %s
                    """, valueClauses).trim();
        }

        static String UPDATE_PAYMENT_ORDER_STATUS = """
                UPDATE payment_orders
                SET payment_order_status = :status, updated_at = CURRENT_TIMESTAMP
                WHERE order_id = :orderId
                """.trim();

        static String UPDATE_PAYMENT_KEY_QUERY = """
                UPDATE payment_events
                SET payment_key = :paymentKey
                WHERE order_id = :orderId
                """.trim();

        static String UPDATE_PAYMENT_EVENT_EXTRA_DETAILS_QUERY = """
                UPDATE payment_events
                SET order_name = :orderName, method = :method, approved_at = :approvedAt, type = :type, updated_at = CURRENT_TIMESTAMP 
                WHERE order_id = :orderId
                """.trim();

        static String INCREMENT_PAYMENT_ORDER_FAILED_COUNT_QUERY = """
                UPDATE payment_orders
                SET failed_count = failed_count + 1
                WHERE order_id = :orderId
              
                """.trim();
    }

}
