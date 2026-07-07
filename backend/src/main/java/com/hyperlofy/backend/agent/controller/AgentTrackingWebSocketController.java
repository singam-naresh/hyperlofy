package com.hyperlofy.backend.agent.controller;

import com.hyperlofy.backend.agent.dto.AgentLocationPayload;
import com.hyperlofy.backend.agent.dto.LiveTrackingResponse;
import com.hyperlofy.backend.agent.service.AgentGeoService;
import com.hyperlofy.backend.common.dto.WebSocketEvent;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.zone.service.GeoLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AgentTrackingWebSocketController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AgentGeoService agentGeoService;
    private final OrderRepository orderRepository;
    private final GeoLocationService geoLocationService;

    @MessageMapping("/agent/track")
    public void handleAgentTracking(AgentLocationPayload payload) {
        log.info("Received Agent Location Stomp message: {}", payload);

        if (payload.getAgentId() == null || payload.getOrderId() == null) {
            log.warn("Invalid agent tracking payload - missing IDs");
            return;
        }

        // 1. Persist coordinates in Redis GEO & Postgres DB history
        agentGeoService.updateAgentLocation(payload.getAgentId(), payload.getLatitude(), payload.getLongitude());

        // 2. Fetch order to compute dynamic distance/ETA metrics
        Optional<Order> orderOpt = orderRepository.findById(payload.getOrderId());
        if (orderOpt.isEmpty()) {
            log.warn("Order not found during tracking: {}", payload.getOrderId());
            return;
        }

        Order order = orderOpt.get();
        double targetLat = order.getDeliveryLatitude();
        double targetLng = order.getDeliveryLongitude();

        // If not yet out for delivery, calculate distance to store for pickup
        if (order.getOrderStatus() == OrderStatus.ACCEPTED || 
            order.getOrderStatus() == OrderStatus.ASSIGNED || 
            order.getOrderStatus() == OrderStatus.PICKED) {
            targetLat = order.getStoreLatitude();
            targetLng = order.getStoreLongitude();
        }

        double distanceRemaining = geoLocationService.calculateDistanceKm(
                payload.getLatitude(), payload.getLongitude(),
                targetLat, targetLng
        );

        // Assume speed ~20 km/h => 3.0 min per km
        double etaMinutes = Math.round(distanceRemaining * 3.0 * 10.0) / 10.0;
        if (etaMinutes < 1.0 && distanceRemaining > 0.05) {
            etaMinutes = 1.0;
        }

        // 3. Build live update payload
        LiveTrackingResponse response = LiveTrackingResponse.builder()
                .orderId(order.getId())
                .agentId(payload.getAgentId())
                .customerId(order.getCustomer().getId())
                .latitude(payload.getLatitude())
                .longitude(payload.getLongitude())
                .distanceRemainingKm(Math.round(distanceRemaining * 100.0) / 100.0)
                .etaMinutes(etaMinutes)
                .orderStatus(order.getOrderStatus().name())
                .timestamp(OffsetDateTime.now())
                .build();

        WebSocketEvent<LiveTrackingResponse> event = WebSocketEvent.<LiveTrackingResponse>builder()
                .eventType("AGENT_LOCATION_UPDATED")
                .payload(response)
                .timestamp(OffsetDateTime.now())
                .build();

        // 4. Multi-cast events to specific STOMP topics
        String orderTopic = "/topic/order/" + order.getId();
        String agentTopic = "/topic/agent/" + payload.getAgentId();
        String customerTopic = "/topic/customer/" + order.getCustomer().getId();
        String adminTopic = "/topic/admin/dashboard";

        simpMessagingTemplate.convertAndSend(orderTopic, event);
        simpMessagingTemplate.convertAndSend(agentTopic, event);
        simpMessagingTemplate.convertAndSend(customerTopic, event);
        simpMessagingTemplate.convertAndSend(adminTopic, event);

        log.debug("Broadcasted tracking coordinates for order {} to topics.", order.getId());
    }
}
