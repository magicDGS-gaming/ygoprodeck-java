package io.github.magicdgs.gaming.ygoprodeck.utils.okhttp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.model.json.JsonConverter;
import io.github.magicdgs.gaming.ygoprodeck.utils.ClientBuilder;
import io.github.magicdgs.gaming.ygoprodeck.utils.okhttp.RateLimitBlockingInterceptor;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.util.List;

public abstract class OkhttpClientBuilder<T> extends ClientBuilder<T> {

    private static final Duration DEFAULT_DURATION = Duration.ofSeconds(1);
    private static final BigDecimal MAX_RATE_PER_MILLIS = computeRatePerMillis(Constants.MAX_REQUEST_PER_SECOND, DEFAULT_DURATION);

    // interceptors are shared
    private OkHttpClient client;
    private ObjectMapper objectMapper;

    private ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = JsonConverter.createObjectMapper(strict);
        }
        return objectMapper;
    }

    private OkHttpClient getOkHttpClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    // TODO: add other interceptors? e.g., Response caching?
                    // TODO: see https://futurestud.io/tutorials/retrofit-2-activate-response-caching-etag-last-modified
                    .addInterceptor(buildRateLimitInterceptor(maxRequest, duration))
                    .build();
        }
        return client;
    }

    protected final T buildInstance() throws YgoprodeckException {
        return doBuildInstance(getObjectMapper(), getOkHttpClient());
    }

    protected abstract T doBuildInstance(final ObjectMapper objectMapper, OkHttpClient client) throws YgoprodeckException;

    private Interceptor buildRateLimitInterceptor(int maxRequest, Duration duration) {
        if (duration == null) {
            return new RateLimitBlockingInterceptor(Constants.DEFAULT_REQUEST_PER_SECOND, DEFAULT_DURATION);
        }
        if (duration.toMillis() <= 0) {
            throw new YgoprodeckException("Durantion for rate-limit cannot be less than ms");
        }
        final BigDecimal currentRate = computeRatePerMillis(maxRequest, duration);
        if (currentRate.compareTo(MAX_RATE_PER_MILLIS) > 0) {
            throw new YgoprodeckException(String.format("Invalid request rate-limit (%s/%s): it exceeds the maximum allowed of %s/second", //
                    maxRequest, duration, Constants.MAX_REQUEST_PER_SECOND));
        }
        return new RateLimitBlockingInterceptor(maxRequest, duration);
    }

    private static BigDecimal computeRatePerMillis(final int maxRequest, final Duration duration) {
        final BigDecimal maxRequestBigDecimal = BigDecimal.valueOf(maxRequest);
        final BigDecimal millisDurationBigDecimal = BigDecimal.valueOf(duration.toMillis());
        try {
            return maxRequestBigDecimal.divide(millisDurationBigDecimal, MathContext.DECIMAL128);
        } catch (final ArithmeticException e) {
            throw new IllegalArgumentException("Unexpected error computing rate per millisecond: " + maxRequest + "/" + millisDurationBigDecimal);
        }
    }
}
