package io.github.magicdgs.gaming.ygProductionYgoprodeckClientParametersITprodeck.integrationtest;

import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.RetrofitClientTester;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseClientTester;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseContractTestSpec;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestClientFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ProductionYgoprodeckClientContractIT extends DatabaseContractTestSpec {

	private static RetrofitClientTester TEST_CLIENT;

	@BeforeAll
	public static void beforeAll() throws Exception {
		log.atDebug().log("Waiting 5 seconds to start the tests");
		TimeUnit.SECONDS.sleep(5);
		TEST_CLIENT = new RetrofitClientTester(RetrofitTestClientFactory.getProductionClient(), false);
	}

	@Override
	protected DatabaseClientTester getClientTester() {
		return TEST_CLIENT;
	}


	@Test
	public void testGetImage() throws Exception {
		var resultCallback = TEST_CLIENT.executeWithCallback(client -> //
				client.getImagesApi().getCardImage(6983839L));
		assertImageResponse(resultCallback.getResponse().get());
	}

	@Test
	public void testGetCardCroppedImage() throws Exception {
		var resultCallback = TEST_CLIENT.executeWithCallback(client -> //
				client.getImagesApi().getCardCroppedImage(27551L));
		assertImageResponse(resultCallback.getResponse().get());
	}

	private static void assertImageResponse(final Response<ResponseBody> response) {
		assertAll(
				() -> assertEquals(200, response.code()),
				() -> assertEquals("image/jpeg", response.headers().get("Content-Type")),
				() -> assertTrue(response.body().bytes().length > 0)
		);
	}

}
