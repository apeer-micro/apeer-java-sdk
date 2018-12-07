package com.apeer.impl;

import com.apeer.IApeerDevKit;
import com.apeer.internal.IFileOutput;
import com.apeer.internal.ISystem;
import com.apeer.internal.OutputJsonFileWriter;
import com.apeer.internal.SystemFacade;
import org.json.JSONException;
import org.json.JSONObject;

public class ApeerDevKit implements IApeerDevKit {
    private final ISystem system;
    private final IFileOutput fileOutputWriter;

    private JSONObject inputJson;
    private JSONObject outputJson;
    private String outputParamsFile;

    public ApeerDevKit() throws ApeerEnvironmentException {
        this(new SystemFacade(), new OutputJsonFileWriter());
    }

    ApeerDevKit(ISystem system, IFileOutput fileOutputWriter) throws ApeerEnvironmentException {
        this.system = system;
        this.fileOutputWriter = fileOutputWriter;

        log("Initializing");

        var wfeInputJsonKey = "WFE_INPUT_JSON";
        var wfe_input_json = this.system.getenv(wfeInputJsonKey);
        if (wfe_input_json == null || wfe_input_json.isBlank()) {
            throw new ApeerEnvironmentException("Could not find \"" + wfeInputJsonKey + "\" in environment variables");
        }

        log("Found \"" + wfeInputJsonKey + "\" to be \"" + wfe_input_json + "\"");

        try {
            inputJson = new JSONObject(wfe_input_json);
            outputParamsFile = inputJson.getString("output_params_file");
        } catch (JSONException ex) {
            throw new ApeerEnvironmentException("Could not decode \"" + wfeInputJsonKey + "\"", ex);
        }

        outputJson = new JSONObject();

        log("Successfully read \"" + wfeInputJsonKey + "\". Output params will be written to \"" + outputParamsFile + "\"");
    }

    public <T> T getInput(String key, Class<T> type) throws ApeerInputException {

        if (!inputJson.has(key)) {
            throw new ApeerInputException("Could not find key \"" + key + "\" in inputs");
        }

        if (type == String.class) {
            return (T) inputJson.getString(key);
        } else if (type == Integer.class || type == int.class) {
            return (T) (Integer) inputJson.getInt(key);
        } else if (type == Double.class || type == double.class) {
            return (T) (Double) inputJson.getDouble(key);
        } else if (type == Boolean.class || type == boolean.class) {
            return (T) (Boolean) inputJson.getBoolean(key);
        } else {
            throw new ApeerInputException("Input type \"" + type.toString() + "\" is not allowed. Use String, Integer, Double or Boolean");
        }
    }

    public void setOutput(String key, Object value) throws ApeerOutputException {
        try {
            outputJson.put(key, value);
        } catch (JSONException ex) {
            throw new ApeerOutputException("Could not set output \"" + key + "\"", ex);
        }
    }

    public void setFileOutput(String key, String outputFilePath) throws ApeerOutputException {
        if (!outputFilePath.startsWith("/output/")) {
            var targetPath = "/output/" + outputFilePath;
            fileOutputWriter.moveFile(outputFilePath, targetPath);
            outputFilePath = targetPath;
        }

        try {
            outputJson.put(key, outputFilePath);
        } catch (JSONException ex) {
            throw new ApeerOutputException("Could not set output \"" + key + "\"", ex);
        }
    }

    public void finalizeModule() {
        var json = outputJson.toString();
        fileOutputWriter.writeTextToFile(outputParamsFile, json);
    }

    private void log(String message) {
        System.out.println("[ADK] " + message);
    }
}
