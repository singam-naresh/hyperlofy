package com.hyperlofy.backend.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketEvent<T> {
    private String eventType;
    private T payload;
    private OffsetDateTime timestamp;
}
