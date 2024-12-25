package com.igorlink.stanleygpt.client.exceptions;

/**
 * Exception thrown when the TTS API client fails to initialize.
 */
public class TtsClientInitException extends RuntimeException {
    public TtsClientInitException(String message) {
        super("Failed to initialize the Silero API client: " + message);
    }
}
