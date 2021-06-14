package com.apeer.sdk;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.file.Path;

import org.json.JSONException;
import org.json.JSONObject;

public class ApeerDevKit {
    public static final String OUTPUT_FILE_PREFIX = "/output/";
    private final ISystem system;
    private final IFileOutput fileOutputWriter;

    private final JSONObject inputJson;
    private final JSONObject outputJson;
    private final String outputParamsFile;

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
            outputParamsFile = inputJson.getString("WFE_output_params_file");
        } catch (JSONException ex) {
            throw new ApeerEnvironmentException("Could not decode \"" + wfeInputJsonKey + "\"", ex);
        }

        outputJson = new JSONObject();

        log("Successfully read \"" + wfeInputJsonKey + "\". Output params will be written to \"" + outputParamsFile + "\"");
    }

    /**
     * Initializes the ADK and reads-in the WFE_INPUT_JSON. <b>NOTE</b> You must call {@code finalizeModule()} after all all other operations on this class
     *
     * @throws ApeerEnvironmentException When the WFE_INPUT_JSON environment variable could either - not be found or - it's value is not a valid JSON or - it
     *                                   does not contain "output_params_file"
     */
    public ApeerDevKit() throws ApeerEnvironmentException {
        this(new SystemFacade(), new OutputJsonFileWriter());
    }

    /**
     * Gets the input from the WFE_INPUT_JSON environment variable
     *
     * @param key  The input key as defined in the module_specification.json of your module
     * @param type The type of the input as defined in the module_specification.json of your module
     * @param <T>  One of (all are possible as array types): String.class, Integer.class, int.class, Double.class, double.class, Boolean.class, bool.class
     * @return The value associated with the key as given by the WFE_INPUT_JSON environment variable
     * @throws ApeerInputException When key could not be found or type is not supported
     */
    @SuppressWarnings("unchecked")
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
        } else if (type.isArray()) {
            var jsonItems = inputJson.getJSONArray(key);
            T items = (T) Array.newInstance(type.getComponentType(), jsonItems.length());
            for (int i = 0; i < jsonItems.length(); i++) {
                var value = jsonItems.get(i);
                if (value instanceof BigDecimal) {
                    Array.set(items, i, ((BigDecimal) value).doubleValue());
                } else {
                    Array.set(items, i, value);
                }
            }
            return items;
        } else {
            throw new ApeerInputException("Input type \"" + type.toString() + "\" is not allowed. Use String, Integer, Double or Boolean");
        }
    }

    /**
     * Sets the output that will be written to output_params_file
     *
     * @param key   The output key as defined in the module_specification.json of your module
     * @param value The value that will be written to the associated key. Can be any type that is supported by JSON but must correspond to the type as specified
     *              in the module_specification.json of your module
     * @throws ApeerOutputException When the value could not be parsed to JSON
     */
    public void setOutput(String key, Object value) throws ApeerOutputException {
        try {
            outputJson.put(key, value);
        } catch (JSONException ex) {
            throw new ApeerOutputException("Could not set output \"" + key + "\"", ex);
        }
    }

    /**
     * Sets a file output that will be written to output_params_file. Also copies the file to the output folder of your module as required by the APEER
     * environment
     *
     * @param key            The output key as defined in the module_specification.json of your module
     * @param outputFilePath The relative path to your file as you saved it
     * @throws ApeerOutputException When the value could not be parsed to JSON
     */
    public void setFileOutput(String key, String outputFilePath) throws ApeerOutputException {
        if (!outputFilePath.startsWith(OUTPUT_FILE_PREFIX)) {
            var targetPath = OUTPUT_FILE_PREFIX + outputFilePath;
            fileOutputWriter.moveFile(Path.of(outputFilePath), Path.of(targetPath));
            outputFilePath = targetPath;
        }

        try {
            outputJson.put(key, outputFilePath);
        } catch (JSONException ex) {
            throw new ApeerOutputException("Could not set output \"" + key + "\"", ex);
        }
    }

    /**
     * Sets multiple file outputs that will be written to output_params_file. Also copies the files to the output folder of your module as required by the APEER
     * environment
     *
     * @param key             The output key as defined in the module_specification.json of your module
     * @param outputFilePaths The relative paths to your files as you saved them
     * @throws ApeerOutputException When the value could not be parsed to JSON
     */
    public void setFileOutput(String key, String[] outputFilePaths) throws ApeerOutputException {
        var targetFilePaths = new String[outputFilePaths.length];
        for (int i = 0; i < outputFilePaths.length; i++) {
            var filePath = outputFilePaths[i];
            if (!filePath.startsWith("/output/")) {
                var targetPath = "/output/" + filePath;
                fileOutputWriter.moveFile(Path.of(filePath), Path.of(targetPath));
                targetFilePaths[i] = targetPath;
            } else {
                targetFilePaths[i] = filePath;
            }
        }

        setOutput(key, targetFilePaths);
    }

    /**
     * Writes all output values as defined via {@code setOutput} and {@code setFileOutput} to the output params file
     *
     * @throws ApeerOutputException When the output params file could not be written
     */
    public void finalizeModule() throws ApeerOutputException {
        var json = outputJson.toString();
        fileOutputWriter.writeTextToFile("/output/" + outputParamsFile, json);
    }

    private void log(String message) {
        System.out.println("[ADK] " + message);
    }
}
