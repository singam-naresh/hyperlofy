package com.hyperlofy.backend.ai.memory.dto;

import com.hyperlofy.backend.ai.memory.MemoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryCreateRequest {

    @NotNull
    private MemoryType memoryType;

    @NotBlank
    private String key;

    @NotBlank
    private String value;

    private Double confidence;
}
