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

    private static ResourceMockServerClientTester MOCK_TESTER;

    protected abstract boolean isAsync();

    @BeforeEach
    public void beforeEach() {
        if (MOCK_TESTER == null) {
            // TODO: change for lenient if we extract model tests somewhere else
            MOCK_TESTER = new ResourceMockServerClientTester(mockServerUrl -> new RetrofitClientTester(
                    // TODO: change for lenient if we extract model tests somewhere else
                    RetrofitTestClientFactory.createMockClient(mockServerUrl, true),
                    isAsync()
            ));
        }
    }

    @AfterAll
    public static void afterAll() {
        if (MOCK_TESTER != null) {
            try {
                MOCK_TESTER.close();
            } catch (final IOException e) {
                log.error("Error closing mock server", e);
            }
            MOCK_TESTER = null;
        }
    }

    @Override
    protected DatabaseClientTester getClientTester() {
        return MOCK_TESTER;
    }
}