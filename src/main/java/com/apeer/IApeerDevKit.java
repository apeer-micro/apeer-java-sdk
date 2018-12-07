package com.apeer;

import com.apeer.impl.ApeerInputException;
import com.apeer.impl.ApeerOutputException;

public interface IApeerDevKit {
    <T> T getInput(String key, Class<T> type) throws ApeerInputException;
    void setOutput(String key, Object value) throws ApeerOutputException;
    void setFileOutput(String key, String outputFilePath) throws ApeerOutputException;
    void finalizeModule() throws ApeerOutputException;
}
