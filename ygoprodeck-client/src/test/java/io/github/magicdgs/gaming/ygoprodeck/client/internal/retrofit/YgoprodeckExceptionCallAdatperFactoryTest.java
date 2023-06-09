package io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit;

import java.io.IOException;
import java.util.Arrays;

import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestApi.TestResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.*;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
public class YgoprodeckExceptionCallAdatperFactoryTest {

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
				.addConverterFactory(JacksonConverterFactory.create())
				.build().create(RetrofitTestApi.class);
	}
	
	@Test
	public void testNoExceptionOnExecuteWithCorrectModel() throws Exception {
		final TestResponse responseObject = TestResponse.builder() //
				.testName("testNoExceptionOnExecuteWithCorrectModel") //
				.build();
		final String responseBody = JsonConverter.asJson(responseObject);
		MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody));
		var response = createApiWithCallAdapterFactory().test().execute();
		Assertions.assertAll(
				() -> Assertions.assertTrue(response.isSuccessful()),
				() -> Assertions.assertDoesNotThrow(() -> response.body())
		);
	}
	
	@Test
	public void testExceptionOnExecuteWhenUnknownModel() throws Exception {
		final String responseBody = JsonConverter.asJson(Arrays.asList("unknown"));
		MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody));
		Assertions.assertThrows(YgoprodeckException.class, //
				() -> createApiWithCallAdapterFactory().test().execute());
	}
	
	@Test
	public void testExceptionOnExecuteWithErrorModel() throws Exception {
		var errorObject = new ErrorDTO();
		errorObject.setError("Dummy error from server response");
		final String responseBody = JsonConverter.asJson(errorObject);
		MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody));
		Assertions.assertThrows(YgoprodeckResponseErrorException.class, //
				() -> createApiWithCallAdapterFactory().test().execute());
	}
	
}
