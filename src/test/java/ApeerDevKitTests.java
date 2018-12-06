import com.apeer.impl.ApeerDevKit;
import com.apeer.impl.ApeerEnvironmentException;
import com.apeer.impl.ApeerInputException;
import com.apeer.internal.ISystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ApeerDevKitTests {

    private ISystem _systemMock;

    @BeforeEach
    void beforeEach() {
        _systemMock = Mockito.mock(ISystem.class);
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeFound() {
        Assertions.assertThrows(ApeerEnvironmentException.class, ApeerDevKit::new);
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonCannotBeDecoded() {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{{");
        Assertions.assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(_systemMock));
    }

    @Test
    void ctor_cannotInitWhenWfeInputJsonIsMissingOutputParamsFile() {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{}");
        Assertions.assertThrows(
                ApeerEnvironmentException.class,
                () -> new ApeerDevKit(_systemMock));
    }

    @Test
    void ctor_initializesWhenWfeInputJsonIsValid() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        new ApeerDevKit(_systemMock);
    }

    @Test
    void getInput_throwsExceptionWhenKeyIsNotInInputs() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON")).thenReturn("{\"output_params_file\":\"out.json\"}");
        var adk = new ApeerDevKit(_systemMock);
        Assertions.assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_throwsExceptionWhenTypeIsNotValidInput() throws ApeerEnvironmentException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"some-key\":42}");
        var adk = new ApeerDevKit(_systemMock);
        Assertions.assertThrows(
                ApeerInputException.class,
                () -> adk.getInput("some-key", Object.class));
    }

    @Test
    void getInput_returnsIntegerFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"integerValue\":42}");
        var adk = new ApeerDevKit(_systemMock);
        var input = adk.getInput("integerValue", int.class);
        Assertions.assertEquals(42, input.intValue());
    }

    @Test
    void getInput_returnsDoubleFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"doubleValue\":42.01}");
        var adk = new ApeerDevKit(_systemMock);
        var input = adk.getInput("doubleValue", double.class);
        Assertions.assertEquals(42.01, input.doubleValue(), Double.MIN_VALUE);
    }

    @Test
    void getInput_returnsBooleanFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"boolValue\":true}");
        var adk = new ApeerDevKit(_systemMock);
        var input = adk.getInput("boolValue", boolean.class);
        Assertions.assertEquals(true, input.booleanValue());
    }

    @Test
    void getInput_returnsStringFromInputs() throws ApeerEnvironmentException, ApeerInputException {
        Mockito.when(_systemMock.getenv("WFE_INPUT_JSON"))
                .thenReturn("{\"output_params_file\":\"out.json\",\"stringValue\":\"Hello ZEISS\"}");
        var adk = new ApeerDevKit(_systemMock);
        var input = adk.getInput("stringValue", String.class);
        Assertions.assertEquals("Hello ZEISS", input);
    }
}
