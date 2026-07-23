package com.hyperlofy.backend.ai.intent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntentRequest {

    @NotBlank(message = "Prompt is required")
    @Size(max = 2000, message = "Prompt must not exceed 2000 characters")
    private String prompt;
}
