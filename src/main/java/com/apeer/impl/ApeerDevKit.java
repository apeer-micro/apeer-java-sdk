package com.apeer.impl;

import com.apeer.IApeerDevKit;
import org.json.JSONObject;

public class ApeerDevKit implements IApeerDevKit {
    private String _output_params_file;

    public ApeerDevKit() {
        var wfe_input_json = System.getenv("WFE_INPUT_JSON");
        var input_json = new JSONObject(wfe_input_json);
        _output_params_file = input_json.getString("output_params_file");
    }

    public void getInputs() {

    }

    public void setOutput() {

    }

    public void setFileOutput() {

    }

    public void finalizeADK() {

    }
}
