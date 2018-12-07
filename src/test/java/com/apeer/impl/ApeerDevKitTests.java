package com.apeer.impl;

import com.apeer.internal.IFileOutput;
import com.apeer.internal.ISystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApeerDevKitTests {

    private ISystem systemMock;
    private IFileOutput fileOutputMock;

    @BeforeEach
    void beforeEach() {
        systemMock = mock(ISystem.class);
        fileOutputMock = mock(IFileOutput.class);
    }

    /*
     * construction
     */

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeFound() {
        assertThrows(ApeerEnvironmentException.class, ApeerDevKit::new);
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeDecoded() {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{{");
        assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(systemMock, fileOutputMock));
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonIsMissingOutputParamsFile() {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{}");
        assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(systemMock, fileOutputMock));
    }

    @Test
    void ctor_initializesWhenWfeInputJsonIsValid() throws ApeerEnvironmentException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        new ApeerDevKit(systemMock, fileOutputMock);
    }

    /*
     * inputs
     */

    @Test
    void getInput_throwsExceptionWhenKeyIsNotInInputs() throws ApeerEnvironmentException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_throwsExceptionWhenTypeIsNotValidInput() throws ApeerEnvironmentException {
        when(systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"some-key\":42}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_returnsIntegerFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        when(systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"integerValue\":42}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        var input = adk.getInput("integerValue", int.class);
        assertEquals(42, input, 1);
    }

    @Test
    void getInput_returnsDoubleFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        when(systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"doubleValue\":42.01}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        var input = adk.getInput("doubleValue", double.class);
        assertEquals(42.01, input, Double.MIN_VALUE);
    }

    @Test
    void getInput_returnsBooleanFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        when(systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"boolValue\":true}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        var input = adk.getInput("boolValue", boolean.class);
        assertEquals(true, input);
    }

    @Test
    void getInput_returnsStringFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        when(systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"stringValue\":\"Hello ZEISS\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);
        var input = adk.getInput("stringValue", String.class);
        assertEquals("Hello ZEISS", input);
    }

    /*
     * outputs
     */

    @Test
    void encodesStringOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);

        adk.setOutput("key_one", "value_one");
        adk.finalizeModule();

        var expectedJson = "{\"key_one\":\"value_one\"}";
        verify(fileOutputMock).writeTextToFile("out.json", expectedJson);
    }

    @Test
    void encodesNumberOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);

        adk.setOutput("key_one", 42);
        adk.setOutput("key_two", 47.11);
        adk.setOutput("key_three", -1e-6);
        adk.finalizeModule();

        var capture = ArgumentCaptor.forClass(String.class);
        verify(fileOutputMock).writeTextToFile(anyString(), capture.capture());
        assertTrue(capture.getValue().contains("\"key_one\":42"));
        assertTrue(capture.getValue().contains("\"key_two\":47.11"));
        assertTrue(capture.getValue().contains("\"key_three\":-1.0E-6"));
    }

    @Test
    void encodesBooleanOutputsToJson() throws ApeerEnvironmentException, ApeerOutputException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);

        adk.setOutput("key_true", true);
        adk.setOutput("key_false", false);
        adk.finalizeModule();

        var capture = ArgumentCaptor.forClass(String.class);
        verify(fileOutputMock).writeTextToFile(anyString(), capture.capture());
        assertTrue(capture.getValue().contains("\"key_true\":true"));
        assertTrue(capture.getValue().contains("\"key_false\":false"));
    }

    /*
     * file outputs
     */

    @Test
    void movesFileToOutputFolder() throws ApeerOutputException, ApeerEnvironmentException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);

        adk.setFileOutput("segmented-image", "path/to/file.png");
        adk.finalizeModule();

        var captureSrc = ArgumentCaptor.forClass(String.class);
        var captureDest = ArgumentCaptor.forClass(String.class);
        verify(fileOutputMock).moveFile(captureSrc.capture(), captureDest.capture());
        assertEquals("path/to/file.png", captureSrc.getValue());
        assertEquals("/output/path/to/file.png", captureDest.getValue());
    }

    @Test
    void doesNotMoveFileToOutputFolderWhenFileAlreadyThere() throws ApeerOutputException, ApeerEnvironmentException {
        when(systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(systemMock, fileOutputMock);

        adk.setFileOutput("segmented-image", "/output/path/to/file.png");
        adk.finalizeModule();

        verify(fileOutputMock, never()).moveFile(anyString(), anyString());
    }
}
