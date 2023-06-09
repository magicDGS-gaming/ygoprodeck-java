package io.github.magicdgs.gaming.ygoprodeck.client.internal.okhttp;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Duration;

@Slf4j
public class RateLimitBlockingInterceptor implements Interceptor {
    private final RateLimiter rateLimiter;

    public RateLimitBlockingInterceptor(final int maxRequest, @NonNull final Duration duration) {
        final RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(duration)
                .limitForPeriod(maxRequest)
                .timeoutDuration(duration)
                .build();
        rateLimiter = RateLimiterRegistry.of(config).rateLimiter(this.getClass().getName());
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        try {
            log.atDebug().setMessage(() -> String.format( //
                    "waiting for request: %s (available=%s;awaiting=%s)",
                    chain.request().url(), //
                    rateLimiter.getMetrics().getAvailablePermissions(),
                    rateLimiter.getMetrics().getNumberOfWaitingThreads())
            ).log();
            return rateLimiter.executeCallable(() -> {
                log.atDebug().setMessage(() -> "proceeding with request: " + chain.request().url()) //
                        .log();
                return chain.proceed(chain.request());
            });
        } catch (final Exception e) {
            return onError(chain, e);
        }
    }

    private Response onError(final Chain chain, final Exception e) {
        // fallback to cancel the call
        chain.call().cancel();
        // return a 429 Too Many Requests (RFC 6585)
        return new Response.Builder() //
                .body(ResponseBody.create("", null))
                .request(chain.request()) //
                .protocol(Protocol.HTTP_1_1) //
                .message(e.getMessage())
                .code(429) //
                .build();
    }
}
