package io.github.magicdgs.gaming.ygoprodeck.integrationtest;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.client.YgoprodeckClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Gets only one instance of the production server so it doesn't get hit too often on tests.
 */
public class ProductionYgoprodeckClientInstance {

    private static YgoprodeckClient INSTANCE;

    /**
     * Gets the instance.
     *
     * @return cached instance.
     */
    public static YgoprodeckClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new YgoprodeckClient.Builder() //
                    .strict(true) //
                    .rateLimit(Constants.DEFAULT_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                    .defaultUrls()
                    .build();
        }
        return INSTANCE;
    }

}
