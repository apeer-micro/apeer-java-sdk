package com.apeer.sdk.impl;

public class ApeerEnvironmentException extends ApeerException {
    ApeerEnvironmentException(String message) {
        super(message);
    }

    ApeerEnvironmentException(String message, Exception inner) {
        super(message, inner);
    }
}
