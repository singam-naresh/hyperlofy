package com.hyperlofy.backend.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard Real-Time WebSocket Message Envelope DTO")
public class WebSocketMessageEnvelopeDTO<T> {

    @Builder.Default
    @Schema(description = "Unique Event ID")
    private String eventId = UUID.randomUUID().toString();

    @Schema(description = "Event Type Header", example = "ORDER_STATUS_UPDATED")
    private String eventType;

    @Builder.Default
    @Schema(description = "ISO-8601 Event Timestamp")
    private String timestamp = Instant.now().toString();

    @Schema(description = "Typed Event Payload Body")
    private T payload;
}
