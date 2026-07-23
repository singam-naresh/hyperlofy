package com.hyperlofy.backend.ai.exception;

public class AiResponseParseException extends AiGatewayException {

    public AiResponseParseException(String message) {
        super(message);
    }

    public AiResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
