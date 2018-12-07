package com.apeer.impl;

import org.apache.commons.lang3.Validate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OutputJsonFileWriter implements IFileOutput {
    @Override
    public void writeTextToFile(String filePath, String text)  throws ApeerOutputException {
        Validate.notNull(filePath);
        Validate.notNull(text);

        FileWriter writer;
        try {
            writer = new FileWriter(filePath);
            writer.append(text);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            throw new ApeerOutputException("Could not write \"" + text + "\" to \"" + filePath + "\"", ex);
        }
    }

    @Override
    public void moveFile(Path sourcePath, Path targetPath)  throws ApeerOutputException {
        Validate.notNull(sourcePath);
        Validate.notNull(targetPath);

        try {
            Files.copy(sourcePath, targetPath);
        } catch (IOException ex) {
            throw new ApeerOutputException("Could not copy \"" + sourcePath + "\" to \"" + targetPath + "\"", ex);
        }
    }
}
