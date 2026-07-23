package com.hyperlofy.backend.ai.exception;

public class AiProviderException extends AiGatewayException {

    public AiProviderException(String message) {
        super(message);
    }

    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
