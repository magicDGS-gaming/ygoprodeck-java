package io.github.magicdgs.gaming.ygoprodeck.client;

import io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit.YgoprodeckExceptionCallAdapterFactory;
import io.github.magicdgs.gaming.ygoprodeck.model.ErrorDTO;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi.TestResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.util.List;

@Slf4j
public class YgoprodeckResultCallbackTest {

	private static MockWebServer MOCK_SERVER;
	
	@BeforeAll
	static void beforeEach() {
		MOCK_SERVER = new MockWebServer();
	}
	
	@AfterAll
	static void afterAll() {
		try {
			MOCK_SERVER.close();
		} catch (final IOException e) {
			log.error("Error closing mock-server", e);
		}
		MOCK_SERVER = null;
	}
	
	private RetrofitTestApi createApiWithCallAdapterFactory() {
		return new Retrofit.Builder()
				.baseUrl(MOCK_SERVER.url("/"))
				.addCallAdapterFactory(YgoprodeckExceptionCallAdapterFactory.create())
				.addConverterFactory(ScalarsConverterFactory.create())
				.addConverterFactory(JacksonConverterFactory.create())
				.build().create(RetrofitTestApi.class);
	}
	
	private YgoprodeckResultCallback<TestResponse> enqueueCallbackAndWait() throws Exception {
		Call<TestResponse> responseCall = createApiWithCallAdapterFactory().test();
		final YgoprodeckResultCallback<TestResponse> testCallback = new YgoprodeckResultCallback<>();
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
		YgoprodeckResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertFalse(resultCallback.getError().isPresent(),
						() -> "Error should not be present: " + resultCallback.getError().get()),
				() -> Assertions.assertFalse(resultCallback.getFailure().isPresent(),
						() -> "Failure should not be present: " + resultCallback.getFailure().get()),
				() -> Assertions.assertTrue(resultCallback.getResponse().isPresent(),
						"Response should be present"),
				() -> Assertions.assertTrue(resultCallback.getResult().isPresent(),
						"Result should be present")
		);
	}
	
	@Test
	public void testErrorModelWhenUnknownModelReturn() throws Exception {
		final String responseBody = JsonConverter.asJson(List.of("unknown"));
		MOCK_SERVER.enqueue(new MockResponse().setResponseCode(200).setBody(responseBody));
		YgoprodeckResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertTrue(resultCallback.getError().isPresent()),
				() -> Assertions.assertTrue(resultCallback.getFailure().isPresent()),
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
		YgoprodeckResultCallback<TestResponse> resultCallback = enqueueCallbackAndWait();
		Assertions.assertAll(
				() -> Assertions.assertTrue(resultCallback.getError().isPresent()),
				() -> Assertions.assertTrue(resultCallback.getFailure().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResponse().isPresent()),
				() -> Assertions.assertFalse(resultCallback.getResult().isPresent())
		);
		Assertions.assertEquals(expectedError, resultCallback.getError().get().getError());
	}
	
}
