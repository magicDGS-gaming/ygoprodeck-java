package io.github.magicdgs.gaming.ygoprodeck.testutils;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.client.YgoprodeckClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RetrofitTestClientFactory {

    private static YgoprodeckClient PROD_CLIENT;

    public static YgoprodeckClient getProductionClient() {
        if (PROD_CLIENT == null) {
            PROD_CLIENT = new YgoprodeckClient.Builder() //
                    .strict(true) //
                    .rateLimit(Constants.DEFAULT_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                    .defaultUrls()
                    .build();
        }
        return PROD_CLIENT;
    }
    public static YgoprodeckClient createMockClient(final String mockServerUrl, final boolean strict) {
        return new YgoprodeckClient.Builder() //
                .strict(strict) //
                .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                .commonUrl(mockServerUrl) //
                .build();
    }

}
