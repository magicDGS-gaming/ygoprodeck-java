package io.github.magicdgs.gaming.ygoprodeck.client.feign;

import java.io.IOException;

import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseClientTester;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseContractTestSpec;
import io.github.magicdgs.gaming.ygoprodeck.testutils.ResourceMockServerClientTester;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;

@Slf4j
public class MockYgoproDeckFeignClientTest extends DatabaseContractTestSpec {

	private static MockWebServer MOCK_SERVER;
	private static DatabaseClientTester MOCK_TESTER;
	
	@BeforeAll
	public static void beforeAll() {
		MOCK_SERVER = new MockWebServer();
		// TODO: change for lenient if we extract model tests somewhere else
		MOCK_TESTER = new ResourceMockServerClientTester(MOCK_SERVER,
			FeignClientTester.createTester(MOCK_SERVER.url("/").toString(), true));
	}
	
	@AfterAll
	public static void afterAll() {
		try {
			MOCK_SERVER.close();
		} catch (final IOException e) {
			log.error("Error closing mock server", e);
		}
		MOCK_SERVER = null;
		MOCK_TESTER = null;
	}

	@Override
	protected DatabaseClientTester getClientTester() {
		return MOCK_TESTER;
	}
	
}
