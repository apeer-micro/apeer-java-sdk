package com.apeer.impl;

import com.apeer.IApeerDevKit;
import com.apeer.internal.ISystem;
import com.apeer.internal.SystemFacade;
import org.json.JSONException;
import org.json.JSONObject;

public class ApeerDevKit implements IApeerDevKit {
    private ISystem _system = new SystemFacade();

    private JSONObject _inputJson;
    private String _outputParamsFile;

    public ApeerDevKit(ISystem system) throws ApeerEnvironmentException {
        _system = system;
        init();
    }

    public ApeerDevKit() throws ApeerEnvironmentException {
        init();
    }

    private void init() throws ApeerEnvironmentException {
        log("Initializing");

        var wfeInputJsonKey = "WFE_INPUT_JSON";
        var wfe_input_json = _system.getenv(wfeInputJsonKey);
        if (wfe_input_json == null || wfe_input_json.isBlank()) {
            throw new ApeerEnvironmentException("Could not find \"" + wfeInputJsonKey + "\" in environment variables");
        }

        log("Found \"" + wfeInputJsonKey + "\" to be \"" + wfe_input_json + "\"");

        try {
            _inputJson = new JSONObject(wfe_input_json);
            _outputParamsFile = _inputJson.getString("output_params_file");
        } catch (JSONException ex) {
            throw new ApeerEnvironmentException("Could not decode \"" + wfeInputJsonKey + "\"", ex);
        }

        log("Successfully read \"" + wfeInputJsonKey + "\". Output params will be written to \"" + _outputParamsFile + "\"");
    }

    public <T> T getInput(String key, Class<T> type) throws ApeerInputException {

        if (!_inputJson.has(key)) {
            throw new ApeerInputException("Could not find key \"" + key + "\" in inputs");
        }

        if (type == String.class) {
            return (T)_inputJson.getString(key);
        } else if (type == Integer.class || type == int.class) {
            return (T)(Integer)_inputJson.getInt(key);
        } else if (type == Double.class || type == double.class) {
            return (T)(Double)_inputJson.getDouble(key);
        } else if (type == Boolean.class || type == boolean.class) {
            return (T)(Boolean)_inputJson.getBoolean(key);
        } else {
            throw new ApeerInputException("Input type \"" + type.toString() + "\" is not allowed. Use String, Integer, Double or Boolean");
        }
    }

    public void setOutput() {

    }

    public void setFileOutput() {

    }

    public void finalizeADK() {

    }

    private void log(String message) {
        System.out.println("[ADK] " + message);
    }
}
