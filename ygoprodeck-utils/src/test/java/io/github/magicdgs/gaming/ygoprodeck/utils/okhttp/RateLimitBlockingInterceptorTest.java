package io.github.magicdgs.gaming.ygoprodeck.utils.okhttp;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RateLimitBlockingInterceptorTest {

    private static final String BEFORE_HEADER = "TestBefore";
    private static final String AFTER_HEADER = "TestAfter";

    private static MockWebServer MOCK_SERVER;

    @BeforeAll
    static void beforeAll() {
        MOCK_SERVER = new MockWebServer();
        log.atDebug().setMessage(() -> "Warm-Up mock server").log();;
        // warmup the mock web-server with some calls
        executeTestCallsAndAssertSuccess(createClient(), 100);
    }

    @AfterAll
    static void afterAll() {
        try {
            MOCK_SERVER.close();
            MOCK_SERVER = null;
        } catch (final IOException e) {
            log.error("Error closing mock-server", e);
        }
    }

    private static OkHttpClient createClient(final Interceptor... interceptors) {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        Arrays.stream(interceptors).forEach(clientBuilder::addInterceptor);
        return clientBuilder.build();
    }

    private static OkHttpClient createClientWithTestInterceptors(final RateLimitBlockingInterceptor interceptor) {
        return createClient(
                chain -> addAfterTimeHeader(chain),
                interceptor,
                chain -> addBeforeTimeHeader(chain));
    }

    private static Response addAfterTimeHeader(final Interceptor.Chain chain) {
        try {
            final okhttp3.Response response = chain.proceed(chain.request());
            final Instant instantAfter = Instant.now();
            return addTimeHeader(response, AFTER_HEADER, instantAfter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Response addBeforeTimeHeader(final Interceptor.Chain chain) {
        try {
            final Instant instantBefore = Instant.now();
            final okhttp3.Response response = chain.proceed(chain.request());
            return addTimeHeader(response, BEFORE_HEADER, instantBefore);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Response addTimeHeader(final okhttp3.Response response, final String header, final Instant instant) {
        return response.newBuilder() //
                .addHeader(header, instant.toString())
                .build();
    }

    private static List<Response> executeTestCallsAndAssertSuccess(final OkHttpClient client, final int nCalls) {
        // enqueue responses first to ensure that the processing time does not affect the test
        return IntStream.range(0, nCalls) //
                .mapToObj(nCall -> {
                    // enqueue a json-like string
                    final String responseBody = String.format("{testData: \"DUMMY-%s\"}", nCall);
                    MOCK_SERVER.enqueue(new MockResponse().setBody(responseBody));
                    return new Request.Builder()
                            .url(MOCK_SERVER.url("/test" + nCall))
                            .build();
                }) //
                .map(request -> {
                    Response response = assertDoesNotThrow(() -> client.newCall(request).execute());
                    assertTrue(response.isSuccessful(), "Not successful :" + response.toString());
                    return response;
                })
                .toList();
    }

    private void assertCallsAfterTimespan(final Duration expectedSpan, final long msBuffer, final Response first, final Response second) {
        // TODO: maybe just pass as a parameter if we wanna to test exactly?
        // span within calls is expected to be the testDuration, but we accept some milliseconds of difference
        final Duration expectedSpanWithBuffer = expectedSpan.minusMillis(msBuffer);
        final Duration actualSpan = calculateTimespan(first, second);

        assertTrue(actualSpan.compareTo(expectedSpanWithBuffer) >= 0, //
                () -> "Expected timespan: " + expectedSpanWithBuffer + "; Actual: " + actualSpan);
    }

    private void assertCallsWithinTimespan(final Duration expectedSpan, final Response first, final Response second) {
        final Instant instantBeforeFirstCall = Instant.parse(first.headers().get(BEFORE_HEADER));
        final Instant instantAfterSecondCall = Instant.parse(second.headers().get(AFTER_HEADER));
        // for this method we don't need to adapt it
        final Duration actualSpan = calculateTimespan(first, second);

        assertTrue(instantBeforeFirstCall.compareTo(instantAfterSecondCall) < 0, //
                () -> "Expected timespan: " + expectedSpan + "; Actual: " + actualSpan);

    }

    private Duration calculateTimespan(final Response first, final Response second) {
        final Instant instantBeforeFirstCall = Instant.parse(first.headers().get(BEFORE_HEADER));
        final Instant instantAfterSecondCall = Instant.parse(second.headers().get(BEFORE_HEADER));
        return Duration.between(instantBeforeFirstCall, instantAfterSecondCall);
    }


    @Test
    public void when_oneMaxRequest_then_secondCallWaits() throws IOException {
        // create the test interceptor and the API
        final int maxRequest = 1;
        final Duration testDuration = Duration.ofSeconds(1);
        final RateLimitBlockingInterceptor testInterceptor = new RateLimitBlockingInterceptor(maxRequest, testDuration);
        final OkHttpClient client = createClientWithTestInterceptors(testInterceptor);

        // execute 2 calls
        List<Response> responses = executeTestCallsAndAssertSuccess(client, 2);

        assertCallsAfterTimespan(testDuration, 30, responses.get(0), responses.get(1));
    }

    @Test
    public void when_twoMaxRequests_then_thirdCallWaits() throws IOException {
        // create the test interceptor and the API
        final int testMaxRequest = 2;
        final Duration testDuration = Duration.ofSeconds(10);
        final RateLimitBlockingInterceptor testInterceptor = new RateLimitBlockingInterceptor(testMaxRequest, testDuration);
        final OkHttpClient client = createClientWithTestInterceptors(testInterceptor);

        // execute 2 calls
        List<Response> responses = executeTestCallsAndAssertSuccess(client, 3);

        // first calls should be happening almost one after the other
        assertCallsWithinTimespan(Duration.ofMillis(100), responses.get(0), responses.get(1));
        // the second call should be happening after the
        assertCallsAfterTimespan(testDuration, 30, responses.get(0), responses.get(2));
        assertCallsAfterTimespan(testDuration, 30, responses.get(1), responses.get(2));
    }

    @Test
    public void when_realisticRateLimitInterceptor_then_100callsDoNotFail() throws IOException {
        // default interceptor should
        final RateLimitBlockingInterceptor testInterceptor = new RateLimitBlockingInterceptor(20, Duration.ofSeconds(1));
        final OkHttpClient client = createClientWithTestInterceptors(testInterceptor);

        // execute 2 calls
        List<Response> responses = executeTestCallsAndAssertSuccess(client, 100);

        assertEquals(responses.size(), 100);
    }

}