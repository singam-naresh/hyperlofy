package com.hyperlofy.backend.ai.orderbuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDraft {
    private String pickup;
    private String drop;
    private String schedule;
    private boolean immediate;
    private String recipient;
    private String phone;
    private String instructions;
    private boolean otp;
    private boolean fragile;
}
