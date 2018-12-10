package com.apeer.sdk.impl;

public class ApeerOutputException extends ApeerException {
    ApeerOutputException(String message, Exception inner) {
        super(message, inner);
    }
}
