package com.hyperlofy.backend.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global API response decorator on system-wide exception handling with Global Error Catalog support.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<GlobalErrorResponse> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String correlationId = MDC.get("correlationId");

        GlobalErrorResponse response = GlobalErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .httpStatus(ex.getStatus().value())
                .errorCode("BUSINESS_ERROR")
                .message(ex.getMessage())
                .developerMessage("Domain business constraint exception thrown.")
                .traceId(traceId)
                .correlationId(correlationId)
                .path(request.getRequestURI())
                .requestMethod(request.getMethod())
                .build();

        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String correlationId = MDC.get("correlationId");

        List<GlobalErrorResponse.ValidationErrorDetail> details = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            Object rejectedVal = ((FieldError) error).getRejectedValue();
            String errorMessage = error.getDefaultMessage();

            details.add(GlobalErrorResponse.ValidationErrorDetail.builder()
                    .field(fieldName)
                    .rejectedValue(rejectedVal != null ? rejectedVal.toString() : "null")
                    .constraintMessage(errorMessage)
                    .build());
        });

        GlobalErrorResponse response = GlobalErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .errorCode(GlobalErrorCode.VALIDATION_001.getErrorCode())
                .message(GlobalErrorCode.VALIDATION_001.getMessage())
                .developerMessage(GlobalErrorCode.VALIDATION_001.getDeveloperMessage())
                .traceId(traceId)
                .correlationId(correlationId)
                .path(request.getRequestURI())
                .requestMethod(request.getMethod())
                .validationErrors(details)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<GlobalErrorResponse> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String correlationId = MDC.get("correlationId");

        GlobalErrorResponse response = GlobalErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .errorCode(GlobalErrorCode.AUTH_005.getErrorCode())
                .message(GlobalErrorCode.AUTH_005.getMessage())
                .developerMessage(GlobalErrorCode.AUTH_005.getDeveloperMessage())
                .traceId(traceId)
                .correlationId(correlationId)
                .path(request.getRequestURI())
                .requestMethod(request.getMethod())
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String correlationId = MDC.get("correlationId");

        GlobalErrorResponse response = GlobalErrorResponse.builder()
                .timestamp(ZonedDateTime.now())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .errorCode(GlobalErrorCode.SYSTEM_001.getErrorCode())
                .message(GlobalErrorCode.SYSTEM_001.getMessage())
                .developerMessage(ex.getMessage())
                .traceId(traceId)
                .correlationId(correlationId)
                .path(request.getRequestURI())
                .requestMethod(request.getMethod())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Getter
    @Builder
    public static class ErrorResponse {
        private final OffsetDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final Map<String, String> details;
    }
}