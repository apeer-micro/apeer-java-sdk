package com.apeer.impl;

import com.apeer.internal.IFileWriter;
import com.apeer.internal.ISystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

class ApeerDevKitTests {

    private ISystem _systemMock;
    private IFileWriter _fileWriterMock;

    @BeforeEach
    void beforeEach() throws ApeerEnvironmentException {
        _systemMock = Mockito.mock(ISystem.class);
        _fileWriterMock = Mockito.mock(IFileWriter.class);
    }

    /*
     * construction
     */

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeFound() {
        Assertions.assertThrows(ApeerEnvironmentException.class, ApeerDevKit::new);
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeDecoded() {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{{");
        Assertions.assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(_systemMock, _fileWriterMock));
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonIsMissingOutputParamsFile() {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{}");
        Assertions.assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(_systemMock, _fileWriterMock));
    }

    @Test
    void ctor_initializesWhenWfeInputJsonIsValid() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        new ApeerDevKit(_systemMock, _fileWriterMock);
    }

    /*
     * inputs
     */

    @Test
    void getInput_throwsExceptionWhenKeyIsNotInInputs() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        Assertions.assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_throwsExceptionWhenTypeIsNotValidInput() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"some-key\":42}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        Assertions.assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_returnsIntegerFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"integerValue\":42}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        var input = adk.getInput("integerValue", int.class);
        Assertions.assertEquals(42, input, 1);
    }

    @Test
    void getInput_returnsDoubleFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"doubleValue\":42.01}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        var input = adk.getInput("doubleValue", double.class);
        Assertions.assertEquals(42.01, input, Double.MIN_VALUE);
    }

    @Test
    void getInput_returnsBooleanFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"boolValue\":true}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        var input = adk.getInput("boolValue", boolean.class);
        Assertions.assertEquals(true, input);
    }

    @Test
    void getInput_returnsStringFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"stringValue\":\"Hello ZEISS\"}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);
        var input = adk.getInput("stringValue", String.class);
        Assertions.assertEquals("Hello ZEISS", input);
    }

    /*
     * outputs
     */

    @Test
    void encodesStringOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);

        adk.setOutput("key_one", "value_one");
        adk.finalizeModule();

        var expectedJson = "{\"key_one\":\"value_one\"}";
        Mockito.verify(_fileWriterMock).writeTextToFile("out.json", expectedJson);
    }

    @Test
    void encodesNumberOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);

        adk.setOutput("key_one", 42);
        adk.setOutput("key_two", 47.11);
        adk.setOutput("key_three", -1e-6);
        adk.finalizeModule();

        var capture = ArgumentCaptor.forClass(String.class);
        Mockito.verify(_fileWriterMock).writeTextToFile(Mockito.anyString(), capture.capture());
        Assertions.assertTrue(capture.getValue().contains("\"key_one\":42"));
        Assertions.assertTrue(capture.getValue().contains("\"key_two\":47.11"));
        Assertions.assertTrue(capture.getValue().contains("\"key_three\":-1.0E-6"));
    }

    @Test
    void encodesBooleanOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(_systemMock, _fileWriterMock);

        adk.setOutput("key_true", true);
        adk.setOutput("key_false", false);
        adk.finalizeModule();

        var capture = ArgumentCaptor.forClass(String.class);
        Mockito.verify(_fileWriterMock).writeTextToFile(Mockito.anyString(), capture.capture());
        Assertions.assertTrue(capture.getValue().contains("\"key_true\":true"));
        Assertions.assertTrue(capture.getValue().contains("\"key_false\":false"));
    }
}
