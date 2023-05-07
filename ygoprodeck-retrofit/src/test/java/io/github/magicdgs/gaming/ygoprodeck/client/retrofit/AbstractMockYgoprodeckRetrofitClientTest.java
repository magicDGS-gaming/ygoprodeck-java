package io.github.magicdgs.gaming.ygoprodeck.client.retrofit;

import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseClientTester;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseContractTestSpec;
import io.github.magicdgs.gaming.ygoprodeck.testutils.ResourceMockServerClientTester;
import io.github.magicdgs.gaming.ygoprodeck.testutils.RetrofitTestClientFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

@Slf4j
abstract class AbstractMockYgoprodeckRetrofitClientTest extends DatabaseContractTestSpec  {

    private static MockWebServer MOCK_SERVER;
    private static DatabaseClientTester MOCK_TESTER;

    protected abstract boolean isAsync();

    @BeforeEach
    public void beforeEach() {
        if (MOCK_SERVER == null) {
            MOCK_SERVER = new MockWebServer();
            // TODO: change for lenient if we extract model tests somewhere else
            final RetrofitClientTester delegate = new RetrofitClientTester(
                    RetrofitTestClientFactory.createMockClient(MOCK_SERVER, true),
                    isAsync()
            );
            MOCK_TESTER = new ResourceMockServerClientTester(MOCK_SERVER, delegate);
        }
    }

    @AfterAll
    public static void afterAll() {
        if (MOCK_SERVER != null) {
            try {
                MOCK_SERVER.close();
            } catch (final IOException e) {
                log.error("Error closing mock server", e);
            }
            MOCK_SERVER = null;
            MOCK_TESTER = null;
        }
    }

    @Override
    protected DatabaseClientTester getClientTester() {
        return MOCK_TESTER;
    }
}