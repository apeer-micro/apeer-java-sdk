package com.apeer.impl;

public class ApeerInputException extends ApeerException {
    ApeerInputException(String message) {
        super(message);
    }

    ApeerInputException(String message, Exception inner) {
        super(message, inner);
    }
}
