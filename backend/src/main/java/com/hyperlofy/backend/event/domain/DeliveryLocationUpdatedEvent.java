package com.hyperlofy.backend.event.domain;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class DeliveryLocationUpdatedEvent extends ApplicationEvent {

    private final UUID orderId;
    private final UUID agentId;
    private final Double latitude;
    private final Double longitude;
    private final Integer estimatedArrivalMinutes;

    public DeliveryLocationUpdatedEvent(Object source, UUID orderId, UUID agentId, Double latitude, Double longitude, Integer estimatedArrivalMinutes) {
        super(source);
        this.orderId = orderId;
        this.agentId = agentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
    }
}
