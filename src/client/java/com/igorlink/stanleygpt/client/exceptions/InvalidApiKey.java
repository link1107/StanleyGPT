package com.igorlink.stanleygpt.client.exceptions;

/**
 * Exception thrown when the API key is invalid.
 */
public class InvalidApiKey extends RuntimeException {
    public InvalidApiKey(String message) {
        super(message);
    }
}
