# APEER Java SDK
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.apeer/apeer-dev-kit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.apeer/apeer-dev-kit)
[![License](https://img.shields.io/badge/Code%20License-MIT-blue.svg)](https://github.com/apeer-micro/apeer-python-sdk/blob/master/LICENSE.txt)

## What it does

Our APEER Java SDK aka. apeer-dev-kit (ADK) is a Java package for reading inputs and writing outputs of APEER modules. The ADK will take care of reading inputs from previous modules in APEER and writing your outputs in the correct format for the next module.

## Installation

Add the apeer-dev-kit as dependency to your pom.xml in your maven project

```xml
<dependency>
  <groupId>com.apeer</groupId>
  <artifactId>apeer-dev-kit</artifactId>
  <version>1.0.4</version>
</dependency>
```

## How to Use

Your code (your_code.java) can be in it's separate package and run totally independent of APEER if you use the following structure.

```java
/*
 * ApeerMain.java
 */
 
var adk = new ApeerDevKit();

var text = adk.getInput("some_text", String.class);
var someNumber = adk.getInput("some_int_number", int.class);

var outputs = new your_code().run(text, someNumber);

adk.setOutput("some_output", outputs.get("some_output_number"));
adk.setFileOutput("some_output_image", (String)outputs.get("some_output_file_path"));
adk.finalizeModule();
 
 
/*
 * YourCode.java
 */
 
public class your_code {
    public Map<String, Object> run(String text, int number) {

        // your processing code goes here ...

        // Make sure you return the outputs as a Map<String, Object> containing all output values as specified in
        // your module's module_specification.json
        var outputs = new HashMap<String, Object>();
        outputs.put("some_output", 123.456);
        outputs.put("some_output_image", "thresholded_image.tiff");
        return outputs;
    }
}
```
