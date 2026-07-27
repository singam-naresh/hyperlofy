package com.hyperlofy.backend.event.listener;

import com.hyperlofy.backend.event.domain.DeliveryLocationUpdatedEvent;
import com.hyperlofy.backend.event.dto.DriverLocationUpdateDTO;
import com.hyperlofy.backend.event.service.RealtimeMessagingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryLocationEventListener {

    private static final Logger log = LoggerFactory.getLogger(DeliveryLocationEventListener.class);
    private final RealtimeMessagingService realtimeMessagingService;

    @Async
    @EventListener
    public void handleDeliveryLocationUpdated(DeliveryLocationUpdatedEvent event) {
        log.info("Async handling DeliveryLocationUpdatedEvent for orderId={}, agentId={}", event.getOrderId(), event.getAgentId());
        DriverLocationUpdateDTO dto = DriverLocationUpdateDTO.builder()
                .orderId(event.getOrderId())
                .agentId(event.getAgentId())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .estimatedArrivalMinutes(event.getEstimatedArrivalMinutes())
                .build();

        realtimeMessagingService.broadcastDriverLocation(dto);
    }
}
