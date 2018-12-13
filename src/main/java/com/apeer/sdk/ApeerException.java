package com.apeer.sdk;

public class ApeerException extends Exception {
    public ApeerException(String message) {
        super("[ADK] " + message);
    }

    public ApeerException(String message, Exception inner) {
        super("[ADK] " + message, inner);
    }
}
