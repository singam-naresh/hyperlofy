package com.hyperlofy.backend.ai.planner.fulfillment;

import com.hyperlofy.backend.agent.dto.LiveTrackingResponse;
import com.hyperlofy.backend.ai.merchantselection.MerchantSelectionResponse;
import com.hyperlofy.backend.ai.recommendation.dto.RecommendationResponse;
import com.hyperlofy.backend.order.dto.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentResponse {
    private boolean success;
    private FulfillmentStatus status;
    private String message;
    private OrderResponse order;
    private LiveTrackingResponse tracking;
    private MerchantSelectionResponse merchantSelection;
    private RecommendationResponse recommendation;
}
