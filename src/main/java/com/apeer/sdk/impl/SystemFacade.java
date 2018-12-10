package com.apeer.sdk.impl;

class SystemFacade implements ISystem {
    @Override
    public String getenv(String key) {
        return System.getenv(key);
    }
}
