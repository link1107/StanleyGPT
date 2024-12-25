package com.igorlink.stanleygpt.client.exceptions;

/**
 * Exception when user's country is not supported by the Open AI API.
 */
public class FailedStateException extends RuntimeException{
    public FailedStateException() {
        super("Open AI API does not support a current location country, you can try using a VPN!");
    }
}
