package io.github.magicdgs.gaming.ygoprodeck.api.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.FeignException;
import feign.Logger;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.magicdgs.gaming.ygoprodeck.testutils.FeignTestApi;
import io.github.magicdgs.gaming.ygoprodeck.testutils.FeignTestApi.TestResponse;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.*;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class YgoprodeckErrorDecoderTest {

    private static MockWebServer MOCK_SERVER;

    @BeforeAll
    static void beforeEach() {
        MOCK_SERVER = new MockWebServer();
    }

    @AfterAll
    static void afterAll() {
        try {
            MOCK_SERVER.close();
            MOCK_SERVER = null;
        } catch (final IOException e) {
            log.error("Error closing mock-server", e);
        }
    }

    private FeignTestApi createApiWithErrorDecoder() {
        final ObjectMapper mapper = JsonConverter.createObjectMapper(true);
        return new Feign.Builder()
                .errorDecoder(new YgoprodeckErrorDecoder(mapper)) //
                .decoder(new YgoprodeckApiResponseDecoder(mapper)) //
                .encoder(new JacksonEncoder(mapper)) //
                .logger(new Slf4jLogger(log)) //
                .logLevel(Logger.Level.FULL) //
                .target(FeignTestApi.class, MOCK_SERVER.url("/").toString());
    }

    @Test
    public void testNoExceptionWithSuccessfulResponseAndKnownModel() throws Exception {
        final FeignTestApi.TestResponse responseObject = FeignTestApi.TestResponse.builder() //
                .testName("testNoExceptionOnExecuteWithCorrectModel") //
                .build();
        final String responseBody = JsonConverter.asJson(responseObject);
        MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody));
        var response = Assertions.assertDoesNotThrow(() -> createApiWithErrorDecoder().test());
        Assertions.assertNotNull(response);
    }

    @Test
    public void testNoExceptionWithSuccessfulHttpInfoResponseAndKnownModel() throws Exception {
        final TestResponse responseObject = TestResponse.builder() //
                .testName("testNoErrorModelExistsWhenCorrectResponse") //
                .build();
        final String responseBody = JsonConverter.asJson(responseObject);
        MOCK_SERVER.enqueue(new MockResponse().setResponseCode(200).setBody(responseBody));
        var actualResponse = createApiWithErrorDecoder().testWithHttpInfo();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(actualResponse.getData(), "Data should be present"),
                () -> Assertions.assertEquals(200, actualResponse.getStatusCode(), "Status code should be correct")
        );
        Assertions.assertEquals(responseObject, actualResponse.getData(), "Data should be the same");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testExceptionWithSuccessfulResponseAndUnknownModel(boolean withHttpInfo) throws Exception {
        final String responseBody = JsonConverter.asJson(Arrays.asList("unknown"));
        MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody).setResponseCode(200));
        final FeignTestApi testApi = createApiWithErrorDecoder();
        final Executable executable = withHttpInfo
                ? testApi::testWithHttpInfo
                : testApi::test;
        // TODO: this use case we should be sure that it is handled on the response decoder
        // TODO: and throw having the data
        var exception = Assertions.assertThrows(FeignException.class, executable);
        Assertions.assertInstanceOf(YgoprodeckException.class, exception.getCause());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testExceptionWithErrorResponseAndKnownErrorModel(boolean withHttpInfo) throws Exception {
        var errorObject = new ErrorDTO();
        errorObject.setError("Dummy error from server response");
        final String responseBody = JsonConverter.asJson(errorObject);
        MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody).setResponseCode(400));
        final FeignTestApi testApi = createApiWithErrorDecoder();
        final Executable executable = withHttpInfo
                ? testApi::testWithHttpInfo
                : testApi::test;
        YgoprodeckResponseErrorException exception = Assertions.assertThrows(YgoprodeckResponseErrorException.class, //
                executable);
        var actualObject = exception.getError();
        Assertions.assertAll(
                () -> Assertions.assertNotSame(errorObject, actualObject),
                () -> Assertions.assertEquals(errorObject, actualObject)
        );
    }
}