package com.apeer.sdk;

public class SystemFacade implements ISystem {
    @Override
    public String getenv(String key) {
        return System.getenv(key);
    }
}
