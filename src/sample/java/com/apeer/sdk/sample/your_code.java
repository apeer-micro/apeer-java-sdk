package com.apeer.sdk.sample;

import java.util.HashMap;
import java.util.Map;

public class your_code {
    public Map<String, Object> run(String inputImagePath, int threshold, String method) {
        System.out.println("Reading input image " + inputImagePath);
        System.out.println("Using threshold     " + threshold);
        System.out.println("with method         " + method);

        var outputs = new HashMap<String, Object>();
        outputs.put("output_image", "thresholded_image.tiff");
        outputs.put("some_other_result_value", 123.654);

        return outputs;
    }
}
