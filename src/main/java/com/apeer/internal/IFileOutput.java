package com.apeer.internal;

public interface IFileOutput {
    void writeTextToFile(String filePath, String text);
    void moveFile(String sourcePath, String targetPath);
}
