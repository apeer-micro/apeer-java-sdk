package com.apeer.sdk.impl;

class ApeerException extends Exception {
    ApeerException(String message) {
        super("[ADK] " + message);
    }

    ApeerException(String message, Exception inner) {
        super("[ADK] " + message, inner);
    }
}
