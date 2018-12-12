package com.apeer.sdk;

public class ApeerEnvironmentException extends ApeerException {
    ApeerEnvironmentException(String message) {
        super(message);
    }

    ApeerEnvironmentException(String message, Exception inner) {
        super(message, inner);
    }
}
