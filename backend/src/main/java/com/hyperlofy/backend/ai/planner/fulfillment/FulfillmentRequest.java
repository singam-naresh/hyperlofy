package com.hyperlofy.backend.ai.planner.fulfillment;

import com.hyperlofy.backend.ai.planner.PlanningResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FulfillmentRequest {

    @NotNull(message = "Planning response is required")
    @Valid
    private PlanningResponse planningResponse;

    private UUID zoneId;
    private Double customerLatitude;
    private Double customerLongitude;
    private String deliveryAddress;
    private String storeName;
    private boolean useWalletPayment = true;
}
