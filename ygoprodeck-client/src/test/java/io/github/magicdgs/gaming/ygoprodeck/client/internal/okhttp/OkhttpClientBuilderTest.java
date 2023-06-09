package io.github.magicdgs.gaming.ygoprodeck.client.internal.okhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class OkhttpClientBuilderTest {

    private OkhttpClientBuilder<Object> testBuilder;

    @BeforeEach
    void beforeEach() {
        testBuilder = new OkhttpClientBuilder<Object>() {
            @Override
            protected Object doBuildInstance(ObjectMapper objectMapper,
                                             OkHttpClient client)
                    throws YgoprodeckException {
                return "Built: " + this.toString();
            }
        };
    }

    @CsvSource({
            "1,50,MILLIS",
            "1,1,SECONDS",
            "1,1,MINUTES",
            "1,1,HOURS"
    })
    @ParameterizedTest
    public void testValidRateForRateLimitInterceptor(final int maxRequests, final long durationAmmount, final ChronoUnit unit) {
        assertDoesNotThrow(() -> //
                testBuilder.defaultUrls() //
                    .rateLimit(maxRequests, Duration.of(durationAmmount, unit)) //
                    .build());
    }

    @CsvSource({
            "1,1,MILLIS",
            "21,1,SECONDS",
            "1500,1,MINUTES"
    })
    @ParameterizedTest
    public void testInvalidRateForRateLimitInterceptor(final int maxRequests, final long durationAmmount, final ChronoUnit unit) {
        assertThrows(YgoprodeckException.class, () -> //
                testBuilder.defaultUrls() //
                        .rateLimit(maxRequests, Duration.of(durationAmmount, unit)) //
                        .build());
    }


}