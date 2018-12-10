package com.apeer.sdk.impl;

import java.nio.file.Path;

interface IFileOutput {
    void writeTextToFile(String filePath, String text) throws ApeerOutputException;
    void moveFile(Path sourcePath, Path targetPath) throws ApeerOutputException;
}
