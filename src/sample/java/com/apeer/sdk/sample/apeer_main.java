package com.apeer.sdk.sample;

import com.apeer.sdk.impl.*;

public class apeer_main {
    public static void main(String[] args) {
        try {
            var adk = new ApeerDevKit();

            var inputImage = adk.getInput("input_image", String.class);
            var threshold = adk.getInput("threshold", int.class);
            var methodName = adk.getInput("method", String.class);

            var outputs = new your_code().run(inputImage, threshold, methodName);

            adk.setOutput("some_output", outputs.get("some_other_result_value"));
            adk.setFileOutput("output_image", (String)outputs.get("output_image"));
            adk.finalizeModule();

        } catch (ApeerEnvironmentException e) {
            e.printStackTrace();
        } catch (ApeerInputException e) {
            e.printStackTrace();
        } catch (ApeerOutputException e) {
            e.printStackTrace();
        }
    }
}
