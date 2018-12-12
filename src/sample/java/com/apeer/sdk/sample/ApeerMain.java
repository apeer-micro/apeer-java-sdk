package com.apeer.sdk.sample;

import com.apeer.sdk.ApeerDevKit;
import com.apeer.sdk.ApeerEnvironmentException;
import com.apeer.sdk.ApeerInputException;
import com.apeer.sdk.ApeerOutputException;

public class ApeerMain {
    public static void main(String[] args) {
        try {
            var adk = new ApeerDevKit();

            var inputImage = adk.getInput("input_image", String.class);
            var threshold = adk.getInput("threshold", int.class);
            var methodName = adk.getInput("method", String.class);

            var outputs = new YourCode().run(inputImage, threshold, methodName);

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
