package io.github.magicdgs.gaming.ygProductionYgoprodeckClientParametersITprodeck.integrationtest;

import io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.client.YgoprodeckClient;
import io.github.magicdgs.gaming.ygoprodeck.integrationtest.ProductionYgoprodeckClientInstance;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseContractTestSpec;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ProductionYgoprodeckClientContractIT extends DatabaseContractTestSpec {

	private static YgoprodeckClient CLIENT;

	@Override
	protected DatabaseApi getDatabaseApi() {
		return CLIENT.getDatabaseApi();
	}

	@BeforeAll
	public static void beforeAll() throws Exception {
		log.atDebug().log("Waiting 5 seconds to start the tests");
		TimeUnit.SECONDS.sleep(5);
		CLIENT = ProductionYgoprodeckClientInstance.getInstance();
	}


	@Test
	public void testGetImage() throws Exception {
		var resultResponse = CLIENT.getImagesApi().getCardImage(6983839L)
				.execute();
		assertImageResponse(resultResponse);
	}

	@Test
	public void testGetCardCroppedImage() throws Exception {
		var resultResponse = CLIENT.getImagesApi().getCardCroppedImage(27551L)
				.execute();
		assertImageResponse(resultResponse);
	}

	private static void assertImageResponse(final Response<ResponseBody> response) {
		assertAll(
				() -> assertEquals(200, response.code()),
				() -> assertEquals("image/jpeg", response.headers().get("Content-Type")),
				() -> assertTrue(response.body().bytes().length > 0)
		);
	}

}
