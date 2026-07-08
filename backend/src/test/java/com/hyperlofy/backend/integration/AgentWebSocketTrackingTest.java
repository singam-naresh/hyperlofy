package com.hyperlofy.backend.integration;

import com.hyperlofy.backend.agent.controller.AgentTrackingWebSocketController;
import com.hyperlofy.backend.agent.dto.AgentLocationPayload;
import com.hyperlofy.backend.agent.service.AgentGeoService;
import com.hyperlofy.backend.order.entity.Order;
import com.hyperlofy.backend.order.entity.OrderStatus;
import com.hyperlofy.backend.order.repository.OrderRepository;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.zone.service.GeoLocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AgentWebSocketTrackingTest {

    @Autowired
    private AgentTrackingWebSocketController trackingController;

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @MockBean
    private AgentGeoService agentGeoService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private GeoLocationService geoLocationService;

    private UUID agentId;
    private UUID orderId;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        agentId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        User customer = User.builder().email("customer@mail.com").build();
        customer.setId(UUID.randomUUID());

        sampleOrder = Order.builder()
                .customer(customer)
                .storeLatitude(12.9716)
                .storeLongitude(77.5946)
                .deliveryLatitude(12.9250)
                .deliveryLongitude(77.6100)
                .orderStatus(OrderStatus.PICKED_AT_STORE)
                .build();
        sampleOrder.setId(orderId);

        Mockito.when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
    }

    @Test
    void testAgentTelemetryProcessingAndDistanceCalculations() {
        AgentLocationPayload payload = AgentLocationPayload.builder()
                .agentId(agentId)
                .orderId(orderId)
                .latitude(12.9500)
                .longitude(77.6000)
                .build();

        Mockito.when(geoLocationService.calculateDistanceKm(
                Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble()
        )).thenReturn(3.50);

        // Execute controller handler logic
        assertDoesNotThrow(() -> {
            trackingController.handleAgentTracking(payload);
        });

        // Verify the location coordinates were persisted asynchronously
        Mockito.verify(agentGeoService, Mockito.times(1))
                .updateAgentLocation(agentId, 12.9500, 77.6000);

        // Verify messaging broadcast template multi-casted the telemetry to key topics
        Mockito.verify(simpMessagingTemplate, Mockito.atLeastOnce())
                .convertAndSend(Mockito.contains("/topic/"), Mockito.any(Object.class));
    }

    @Test
    void testGeofenceDistanceVerificationServiceSuccess() {
        GeoLocationService service = new GeoLocationService();
        // Distance between Bangalore city coordinates (12.9716, 77.5946) and Indiranagar (12.9719, 77.6412) is ~5.07 Km
        double dist = service.calculateDistanceKm(12.9716, 77.5946, 12.9719, 77.6412);
        assertTrue(dist > 4.9 && dist < 5.2);

        // Geofence check inside 6.0 Km radius boundary
        boolean withinGeofence = service.isWithinRadius(12.9716, 77.5946, 12.9719, 77.6412, 6.0);
        assertTrue(withinGeofence);

        // Outside 4.0 Km radius boundary
        boolean outsideGeofence = service.isWithinRadius(12.9716, 77.5946, 12.9719, 77.6412, 4.0);
        assertFalse(outsideGeofence);
    }
}
