package com.apeer.sdk;

public class ApeerEnvironmentException extends ApeerException {
    public ApeerEnvironmentException(String message) {
        super(message);
    }

    public ApeerEnvironmentException(String message, Exception inner) {
        super(message, inner);
    }
}
