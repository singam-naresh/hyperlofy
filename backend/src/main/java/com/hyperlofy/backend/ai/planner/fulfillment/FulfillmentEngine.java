package com.hyperlofy.backend.ai.planner.fulfillment;

public interface FulfillmentEngine {
    FulfillmentResponse orchestrate(FulfillmentRequest request);
}
