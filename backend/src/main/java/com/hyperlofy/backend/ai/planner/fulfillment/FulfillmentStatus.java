package com.hyperlofy.backend.ai.planner.fulfillment;

public enum FulfillmentStatus {
    SHOPPING_ORDER_CREATED,
    PAYMENT_RESERVED,
    HELPER_TASK_CREATED,
    HELPER_ASSIGNED,
    TRACKING_READY,
    NO_ACTION_REQUIRED,
    ERROR
}
