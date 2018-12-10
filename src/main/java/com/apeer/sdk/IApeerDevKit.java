package com.apeer.sdk;

import com.apeer.sdk.impl.ApeerInputException;
import com.apeer.sdk.impl.ApeerOutputException;

public interface IApeerDevKit {
    <T> T getInput(String key, Class<T> type) throws ApeerInputException;
    void setOutput(String key, Object value) throws ApeerOutputException;
    void setFileOutput(String key, String outputFilePath) throws ApeerOutputException;
    void finalizeModule() throws ApeerOutputException;
}
