package com.hyperlofy.backend.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class BusinessMetricsService {

    private final Counter ordersCreatedCounter;
    private final Counter ordersDeliveredCounter;
    private final Counter ordersCancelledCounter;
    private final Counter refundCompletedCounter;
    private final Counter escrowReleasedCounter;

    public BusinessMetricsService(MeterRegistry registry) {
        this.ordersCreatedCounter = Counter.builder("hyperlofy.orders.created")
                .description("Total count of orders created")
                .register(registry);

        this.ordersDeliveredCounter = Counter.builder("hyperlofy.orders.delivered")
                .description("Total count of orders delivered successfully")
                .register(registry);

        this.ordersCancelledCounter = Counter.builder("hyperlofy.orders.cancelled")
                .description("Total count of cancelled orders")
                .register(registry);

        this.refundCompletedCounter = Counter.builder("hyperlofy.refunds.completed")
                .description("Total count of completed refunds")
                .register(registry);

        this.escrowReleasedCounter = Counter.builder("hyperlofy.escrow.released")
                .description("Total count of released escrow pools")
                .register(registry);
    }

    public void incrementOrdersCreated() {
        ordersCreatedCounter.increment();
    }

    public void incrementOrdersDelivered() {
        ordersDeliveredCounter.increment();
    }

    public void incrementOrdersCancelled() {
        ordersCancelledCounter.increment();
    }

    public void incrementRefundCompleted() {
        refundCompletedCounter.increment();
    }

    public void incrementEscrowReleased() {
        escrowReleasedCounter.increment();
    }
}
