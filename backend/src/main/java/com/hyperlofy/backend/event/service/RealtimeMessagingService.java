package com.hyperlofy.backend.event.service;

import com.hyperlofy.backend.event.dto.DriverLocationUpdateDTO;
import com.hyperlofy.backend.event.dto.OrderStatusEventDTO;
import com.hyperlofy.backend.event.dto.WebSocketMessageEnvelopeDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RealtimeMessagingService {

    private static final Logger log = LoggerFactory.getLogger(RealtimeMessagingService.class);
    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastOrderStatus(OrderStatusEventDTO dto) {
        WebSocketMessageEnvelopeDTO<OrderStatusEventDTO> envelope = WebSocketMessageEnvelopeDTO.<OrderStatusEventDTO>builder()
                .eventType("ORDER_STATUS_UPDATED")
                .payload(dto)
                .build();

        // Broadcast to specific order channel
        messagingTemplate.convertAndSend("/topic/orders/" + dto.getOrderId(), envelope);

        // Broadcast to merchant channel
        if (dto.getMerchantId() != null) {
            messagingTemplate.convertAndSend("/topic/merchants/" + dto.getMerchantId() + "/orders", envelope);
        }

        // Broadcast to driver channel
        if (dto.getAgentId() != null) {
            messagingTemplate.convertAndSend("/topic/agents/" + dto.getAgentId() + "/orders", envelope);
        }

        // Broadcast to admin dashboard
        messagingTemplate.convertAndSend("/topic/admin/orders", envelope);

        log.info("Broadcasted real-time order status update for orderId={}, newStatus={}", dto.getOrderId(), dto.getNewStatus());
    }

    public void broadcastDriverLocation(DriverLocationUpdateDTO dto) {
        WebSocketMessageEnvelopeDTO<DriverLocationUpdateDTO> envelope = WebSocketMessageEnvelopeDTO.<DriverLocationUpdateDTO>builder()
                .eventType("DRIVER_LOCATION_UPDATED")
                .payload(dto)
                .build();

        messagingTemplate.convertAndSend("/topic/orders/" + dto.getOrderId() + "/tracking", envelope);
        messagingTemplate.convertAndSend("/topic/admin/tracking", envelope);

        log.info("Broadcasted live driver location update for orderId={}, lat={}, lng={}", dto.getOrderId(), dto.getLatitude(), dto.getLongitude());
    }

    public void sendPrivateNotification(UUID userId, Object notificationPayload) {
        WebSocketMessageEnvelopeDTO<Object> envelope = WebSocketMessageEnvelopeDTO.builder()
                .eventType("SYSTEM_NOTIFICATION")
                .payload(notificationPayload)
                .build();

        messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", envelope);
        log.info("Sent private WebSocket notification to userId={}", userId);
    }
}
