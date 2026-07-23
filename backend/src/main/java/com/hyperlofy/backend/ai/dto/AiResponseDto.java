package com.hyperlofy.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    private String provider;
    private String model;
    private Object content;
    private OffsetDateTime timestamp;
}
