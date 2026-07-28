package com.hyperlofy.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalErrorResponse {

    @Builder.Default
    private ZonedDateTime timestamp = ZonedDateTime.now();

    private int httpStatus;
    private String errorCode;
    private String message;
    private String developerMessage;
    private String traceId;
    private String correlationId;
    private String path;
    private String requestMethod;

    private List<ValidationErrorDetail> validationErrors;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDetail {
        private String field;
        private String rejectedValue;
        private String constraintMessage;
    }
}
