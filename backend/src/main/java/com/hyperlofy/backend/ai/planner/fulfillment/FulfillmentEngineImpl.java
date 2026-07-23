package com.hyperlofy.backend.ai.planner.fulfillment;

import com.hyperlofy.backend.ai.planner.PlanningResponse;
import com.hyperlofy.backend.ai.orderbuilder.OrderBuilderResponse;
import com.hyperlofy.backend.ai.orderbuilder.OrderDraft;
import com.hyperlofy.backend.agent.dto.LiveTrackingResponse;
import com.hyperlofy.backend.common.exception.BusinessException;
import com.hyperlofy.backend.order.dto.OrderRequest;
import com.hyperlofy.backend.order.dto.OrderResponse;
import com.hyperlofy.backend.order.service.AssignmentService;
import com.hyperlofy.backend.order.service.OrderService;
import com.hyperlofy.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FulfillmentEngineImpl implements FulfillmentEngine {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AssignmentService assignmentService;

    @Override
    public FulfillmentResponse orchestrate(FulfillmentRequest request) {
        try {
            PlanningResponse planning = request.getPlanningResponse();
            if (isShoppingFlow(planning)) {
                return executeShoppingFlow(request);
            }
            if (isHelperFlow(planning)) {
                return executeHelperFlow(request);
            }
            return FulfillmentResponse.builder()
                    .success(false)
                    .status(FulfillmentStatus.NO_ACTION_REQUIRED)
                    .message("No supported flow detected for fulfillment")
                    .build();
        } catch (Exception ex) {
            log.error("Fulfillment orchestration failed", ex);
            return FulfillmentResponse.builder()
                    .success(false)
                    .status(FulfillmentStatus.ERROR)
                    .message(ex.getMessage())
                    .build();
        }
    }

    private boolean isShoppingFlow(PlanningResponse planning) {
        if (planning == null || planning.getConversation() == null) {
            return false;
        }
        String intention = planning.getConversation().getIntent();
        return intention != null && (intention.equals("GROCERY") || intention.equals("MEDICINE") || intention.equals("ELECTRONICS") || intention.equals("FOOD") || intention.equals("CAKE") || intention.equals("FLOWERS") || intention.equals("PET_SUPPLIES"));
    }

    private boolean isHelperFlow(PlanningResponse planning) {
        if (planning == null || planning.getConversation() == null) {
            return false;
        }
        String plan = planning.getConversation().getPlan();
        return plan != null && plan.equals("AI_HELPER_CONCIERGE");
    }

    private FulfillmentResponse executeShoppingFlow(FulfillmentRequest request) {
        PlanningResponse planning = request.getPlanningResponse();
        OrderBuilderResponse orderDraft = planning.getOrderDraft();
        if (orderDraft == null || !orderDraft.isSuccess() || orderDraft.getDraft() == null) {
            return FulfillmentResponse.builder()
                    .success(false)
                    .status(FulfillmentStatus.ERROR)
                    .message("Order draft is missing or invalid for shopping fulfillment")
                    .build();
        }

        OrderDraft draft = orderDraft.getDraft();
        OrderRequest orderRequest = buildOrderRequest(request, draft);
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        if (request.isUseWalletPayment()) {
            paymentService.initiateWalletPayment(orderResponse.getId());
        } else {
            assignmentService.assignAgentToOrder(orderResponse.getId());
        }

        LiveTrackingResponse tracking = buildTrackingResponse(orderResponse);
        return FulfillmentResponse.builder()
                .success(true)
                .status(FulfillmentStatus.TRACKING_READY)
                .message("Shopping order created, payment reserved, helper assigned")
                .order(orderResponse)
                .tracking(tracking)
                .merchantSelection(planning.getMerchantSelection())
                .recommendation(planning.getRecommendation())
                .build();
    }

    private FulfillmentResponse executeHelperFlow(FulfillmentRequest request) {
        PlanningResponse planning = request.getPlanningResponse();
        OrderBuilderResponse orderDraft = planning.getOrderDraft();

        if (orderDraft == null || !orderDraft.isSuccess() || orderDraft.getDraft() == null) {
            return FulfillmentResponse.builder()
                    .success(false)
                    .status(FulfillmentStatus.ERROR)
                    .message("Helper flow requires a valid helper order draft")
                    .build();
        }

        OrderDraft draft = orderDraft.getDraft();
        OrderRequest orderRequest = buildHelperOrderRequest(request, draft);
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        assignmentService.assignAgentToOrder(orderResponse.getId());

        LiveTrackingResponse tracking = buildTrackingResponse(orderResponse);
        return FulfillmentResponse.builder()
                .success(true)
                .status(FulfillmentStatus.HELPER_ASSIGNED)
                .message("Helper task created and helper assigned")
                .order(orderResponse)
                .tracking(tracking)
                .recommendation(planning.getRecommendation())
                .build();
    }

    private OrderRequest buildOrderRequest(FulfillmentRequest request, OrderDraft draft) {
        if (draft.getOrderType() == null || !draft.getOrderType().equals("SHOPPING")) {
            throw new BusinessException("Unsupported draft type for full order creation", HttpStatus.BAD_REQUEST);
        }

        String itemsDesc = draft.getItems().stream()
                .map(item -> item.getQuantity() + " x " + item.getItemName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Shopping items");

        return OrderRequest.builder()
                .customerId(draft.getCustomerId())
                .zoneId(request.getZoneId())
                .storeName(request.getStoreName() == null ? "Hyperlofy Store" : request.getStoreName())
                .storeLatitude(request.getCustomerLatitude() == null ? 0.0 : request.getCustomerLatitude())
                .storeLongitude(request.getCustomerLongitude() == null ? 0.0 : request.getCustomerLongitude())
                .deliveryAddress(request.getDeliveryAddress() == null ? "Customer address" : request.getDeliveryAddress())
                .deliveryLatitude(request.getCustomerLatitude() == null ? 0.0 : request.getCustomerLatitude())
                .deliveryLongitude(request.getCustomerLongitude() == null ? 0.0 : request.getCustomerLongitude())
                .itemsDesc(itemsDesc)
                .build();
    }

    private OrderRequest buildHelperOrderRequest(FulfillmentRequest request, OrderDraft draft) {
        if (draft.getDeliveryDetails() == null) {
            throw new BusinessException("Helper flow requires delivery details in draft", HttpStatus.BAD_REQUEST);
        }

        return OrderRequest.builder()
                .customerId(draft.getCustomerId())
                .zoneId(request.getZoneId())
                .storeName(request.getStoreName() == null ? "Helper Service" : request.getStoreName())
                .storeLatitude(request.getCustomerLatitude() == null ? 0.0 : request.getCustomerLatitude())
                .storeLongitude(request.getCustomerLongitude() == null ? 0.0 : request.getCustomerLongitude())
                .deliveryAddress(request.getDeliveryAddress() == null ? draft.getDeliveryDetails().getDrop() : request.getDeliveryAddress())
                .deliveryLatitude(request.getCustomerLatitude() == null ? 0.0 : request.getCustomerLatitude())
                .deliveryLongitude(request.getCustomerLongitude() == null ? 0.0 : request.getCustomerLongitude())
                .itemsDesc(draft.getDeliveryDetails().getInstructions() == null ? "Helper delivery task" : draft.getDeliveryDetails().getInstructions())
                .build();
    }

    private LiveTrackingResponse buildTrackingResponse(OrderResponse order) {
        return LiveTrackingResponse.builder()
                .orderId(order.getId())
                .agentId(order.getAgentId())
                .customerId(order.getCustomerId())
                .latitude(order.getDeliveryLatitude() == null ? 0.0 : order.getDeliveryLatitude())
                .longitude(order.getDeliveryLongitude() == null ? 0.0 : order.getDeliveryLongitude())
                .distanceRemainingKm(0.0)
                .etaMinutes(0.0)
                .orderStatus(order.getOrderStatus() == null ? null : order.getOrderStatus().name())
                .timestamp(java.time.OffsetDateTime.now())
                .build();
    }
}
