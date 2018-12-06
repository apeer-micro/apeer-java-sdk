package com.apeer;

import com.apeer.impl.ApeerInputException;

public interface IApeerDevKit {
    <T> T getInput(String key, Class<T> type) throws ApeerInputException;
    void setOutput();
    void setFileOutput();
    void finalizeADK();
}
