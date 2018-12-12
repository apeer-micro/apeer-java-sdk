package com.apeer.sdk;

import com.apeer.sdk.ApeerOutputException;

import java.nio.file.Path;

public interface IFileOutput {
    void writeTextToFile(String filePath, String text) throws ApeerOutputException;
    void moveFile(Path sourcePath, Path targetPath) throws ApeerOutputException;
}
