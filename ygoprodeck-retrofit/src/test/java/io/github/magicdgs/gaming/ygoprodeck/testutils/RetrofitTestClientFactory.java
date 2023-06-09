package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.RetrofitClientTester;
import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import okhttp3.mockwebserver.MockWebServer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RetrofitTestClientFactory {

    private static YgoprodeckRetrofitClient PROD_CLIENT;

    public static YgoprodeckRetrofitClient getProductionClient() {
        if (PROD_CLIENT == null) {
            PROD_CLIENT = new YgoprodeckRetrofitClient.Builder() //
                    .strict(true) //
                    .rateLimit(Constants.DEFAULT_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                    .defaultUrls()
                    .build();
        }
        return PROD_CLIENT;
    }
    public static YgoprodeckRetrofitClient createMockClient(final String mockServerUrl, final boolean strict) {
        return new YgoprodeckRetrofitClient.Builder() //
                .strict(strict) //
                .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                .commonUrl(mockServerUrl) //
                .build();
    }

}
