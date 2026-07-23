package com.hyperlofy.backend.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestDto {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    private String provider;
    private String model;
    private String systemPrompt;
}
