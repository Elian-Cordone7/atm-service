package com.link.atm.console.client;


public class BackendClientException extends RuntimeException {
    public BackendClientException(String message) {
        super(message);
    }

    public BackendClientException(String message, Throwable cause) {
        super(message, cause);
    }
}