package com.igorlink.stanleygpt.client.exceptions;

/**
 * Exception thrown when the GPT API client fails to initialize.
 */
public class GptClientInitException extends RuntimeException {
    public GptClientInitException(String message) {
        super("Failed to initialize the GPT API client: " + message);
    }
}
