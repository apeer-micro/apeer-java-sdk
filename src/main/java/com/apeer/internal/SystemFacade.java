package com.apeer.internal;

public class SystemFacade implements ISystem {
    @Override
    public String getenv(String key) {
        return System.getenv(key);
    }
}
