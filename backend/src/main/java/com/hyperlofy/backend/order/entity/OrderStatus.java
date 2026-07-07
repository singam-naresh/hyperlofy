package com.hyperlofy.backend.order.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_SUCCESS,
    ASSIGNED,
    PICKED_AT_STORE,
    OUT_FOR_DELIVERY,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    REFUND_INITIATED,
    REFUNDED;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new HashMap<>();

    static {
        VALID_TRANSITIONS.put(CREATED, EnumSet.of(PAYMENT_PENDING, CANCELLED));
        VALID_TRANSITIONS.put(PAYMENT_PENDING, EnumSet.of(PAYMENT_SUCCESS, CANCELLED));
        VALID_TRANSITIONS.put(PAYMENT_SUCCESS, EnumSet.of(ASSIGNED, CANCELLED, REFUND_INITIATED));
        VALID_TRANSITIONS.put(ASSIGNED, EnumSet.of(PICKED_AT_STORE, CANCELLED, REFUND_INITIATED));
        VALID_TRANSITIONS.put(PICKED_AT_STORE, EnumSet.of(OUT_FOR_DELIVERY));
        VALID_TRANSITIONS.put(OUT_FOR_DELIVERY, EnumSet.of(DELIVERED));
        VALID_TRANSITIONS.put(DELIVERED, EnumSet.of(COMPLETED));
        VALID_TRANSITIONS.put(CANCELLED, EnumSet.of(REFUND_INITIATED));
        VALID_TRANSITIONS.put(REFUND_INITIATED, EnumSet.of(REFUNDED));
        // COMPLETED and REFUNDED are terminal states
        VALID_TRANSITIONS.put(COMPLETED, EnumSet.noneOf(OrderStatus.class));
        VALID_TRANSITIONS.put(REFUNDED, EnumSet.noneOf(OrderStatus.class));
    }

    /**
     * Checks if transitioning from this state to the nextState is valid.
     */
    public boolean canTransitionTo(OrderStatus nextState) {
        Set<OrderStatus> allowed = VALID_TRANSITIONS.get(this);
        return allowed != null && allowed.contains(nextState);
    }
}
