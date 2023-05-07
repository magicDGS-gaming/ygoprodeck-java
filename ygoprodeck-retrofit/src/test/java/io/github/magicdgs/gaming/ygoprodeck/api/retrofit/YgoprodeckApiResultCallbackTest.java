package io.github.magicdgs.gaming.ygoprodeck.api.retrofit;

import java.io.IOException;
import java.util.Arrays;

import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi.TestResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

@Slf4j
public class YgoprodeckApiResultCallbackTest {

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
	
	private RetrofitTestApi createApiWithCallAdapterFactory() {
		return new Retrofit.Builder()
				.baseUrl(MOCK_SERVER.url("/"))
				.addCallAdapterFactory(YgoprodeckExceptionCallAdapterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create())
				.build().create(RetrofitTestApi.class);
	}
	
	private YgoprodeckApiResultCallback<TestResponse> enqueueCallbackAndWait() throws Exception {
		Call<TestResponse> responseCall = createApiWithCallAdapterFactory().test();
		final YgoprodeckApiResultCallback<TestResponse> testCallback = new YgoprodeckApiResultCallback<>();
		responseCall.enqueue(testCallback);
		testCallback.awaitResult();
		return testCallback;
	}
	
	@Test
	public void testNoErrorModelExistsWhenCorrectResponse() throws Exception {
		final TestResponse responseObject = TestResponse.builder() //
				.testName("testNoErrorModelExistsWhenCorrectResponse") //
				.build();
		final String responseBody = JsonConverter.asJson(responseObject);
		MOCK_SERVER.enqueue(new MockResponse().setResponseCode(200).setBody(responseBody));
		YgoprodeckApiResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertFalse(resultCallback.getError().isPresent(), () -> "Error should not be present: " + resultCallback.getError().get()),
				() -> Assertions.assertTrue(resultCallback.getResponse().isPresent(), "Response should be present"),
				() -> Assertions.assertTrue(resultCallback.getResult().isPresent(), "Result should be present")
		);
	}
	
	@Test
	public void testErrorModelWhenUnknownModelReturn() throws Exception {
		final String responseBody = JsonConverter.asJson(Arrays.asList("unknown"));
		MOCK_SERVER.enqueue(new MockResponse().setResponseCode(200).setBody(responseBody));
		YgoprodeckApiResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertTrue(resultCallback.getError().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResponse().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResult().isPresent())
		);
		final String errorMsg = resultCallback.getError().get().getError();
		Assertions.assertTrue(errorMsg.contains("TestResponse"), () -> "Error expected to contain error mentioning TestResponse serialization error but was " + errorMsg);
	}
	
	@Test
	public void testErrorModelWhenErrorModelReturn() throws Exception {
		final String expectedError = "Dummy error from server response";
		var errorObject = new ErrorDTO();
		errorObject.setError(expectedError);
		final String responseBody = JsonConverter.asJson(errorObject);
		MOCK_SERVER.enqueue(new MockResponse().setResponseCode(400).setBody(responseBody));
		YgoprodeckApiResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertTrue(resultCallback.getError().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResponse().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResult().isPresent())
		);
		Assertions.assertEquals(expectedError, resultCallback.getError().get().getError());
	}
	
}
