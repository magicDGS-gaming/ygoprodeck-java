package io.github.magicdgs.gaming.ygoprodeck.client;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseContractTestSpec;
import io.github.magicdgs.gaming.ygoprodeck.testutils.YgoprodeckMockServerFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public class YgoprodeckMockClientTest extends DatabaseContractTestSpec {

    private static MockWebServer MOCK_SERVER;
    private static YgoprodeckClient MOCK_CLIENT;

    @BeforeAll
    public static void beforeEach() {
        if (MOCK_SERVER == null) {
            MOCK_SERVER = YgoprodeckMockServerFactory.createMockWebServer();
            // TODO: change for lenient if we extract model tests somewhere else
            MOCK_CLIENT = new YgoprodeckClient.Builder() //
                    .strict(true) //
                    .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                    .commonUrl(MOCK_SERVER.url("/").toString()) //
                    .build();
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
        }
    }

    @Override
    protected DatabaseApi getDatabaseApi() {
        return MOCK_CLIENT.getDatabaseApi();
    }

}
