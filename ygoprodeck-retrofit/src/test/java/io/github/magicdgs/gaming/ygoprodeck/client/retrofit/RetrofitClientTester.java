package io.github.magicdgs.gaming.ygoprodeck.client.retrofit;

import io.github.magicdgs.gaming.ygoprodeck.Constants;
import io.github.magicdgs.gaming.ygoprodeck.api.retrofit.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.api.retrofit.YgoprodeckApiResultCallback;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.testutils.DatabaseClientTester;
import lombok.RequiredArgsConstructor;
import retrofit2.Call;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static io.github.magicdgs.gaming.ygoprodeck.utils.DatabaseApiQueryUtils.*;

@RequiredArgsConstructor
public class RetrofitClientTester implements DatabaseClientTester {

    private final YgoprodeckRetrofitClient client;
    private final boolean useAsync;

    /**
    public static RetrofitClientTester createProductionTester(final boolean strict, final boolean useAsync) {
        var client = new YgoprodeckRetrofitClient.Builder() //
                .strict(strict) //
                .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                .defaultUrls()
                .build();
        return new RetrofitClientTester(client, useAsync);
    }

    public static RetrofitClientTester createTester(final String url, final boolean strict, final boolean useAsync) {
        var client = new YgoprodeckRetrofitClient.Builder() //
                .strict(strict) //
                .rateLimit(Constants.MAX_REQUEST_PER_SECOND, Duration.of(1, ChronoUnit.SECONDS))
                .commonUrl(url) //
                .build();
        return new RetrofitClientTester(client, useAsync);
    }
     **/

    private static <T> T executeSync(final Call<T> call) throws Exception {
        return call.execute().body();
    }

    private static <T> YgoprodeckApiResultCallback<T> executeAsync(final Call<T> call,
                                                            boolean expectedSuccess)
            throws Exception {
        final var callback = new YgoprodeckApiResultCallback<T>();
        call.enqueue(callback);
        callback.awaitResult();
        assertAll(
                () -> assertEquals(expectedSuccess, callback.getResponse().isPresent()),
                () -> assertEquals(expectedSuccess, callback.getResult().isPresent()),
                () -> assertEquals(expectedSuccess, callback.getError().isEmpty()),
                () -> assertEquals(expectedSuccess, callback.getFailure().isEmpty())
        );

        if (!expectedSuccess) {
            throw (Exception) callback.getFailure().get();
        }
        return callback;
    }

    public <T> YgoprodeckApiResultCallback<T> executeWithCallback(final Function<YgoprodeckRetrofitClient, Call<T>> callFunction)
            throws Exception {
        return executeWithCallback(callFunction, true);
    }

    public <T> YgoprodeckApiResultCallback<T> executeWithCallback(final Function<YgoprodeckRetrofitClient, Call<T>> callFunction,
                                                                     boolean expectedSuccess)
            throws Exception {
        return executeAsync(callFunction.apply(client), expectedSuccess);
    }

    protected <T> T executeCall(final Function<YgoprodeckRetrofitClient, Call<T>> callFunction)
            throws Exception {
        return executeCall(callFunction, true);
    }

    protected <T> T executeCall(final Function<YgoprodeckRetrofitClient, Call<T>> callFunction,
                             boolean expectedSuccess)
            throws Exception {
        return useAsync
                ? executeAsync(callFunction.apply(client), expectedSuccess).getResult().get()
                : executeSync(callFunction.apply(client));
    }

    @Override
    public List<CheckDBVersionDTO> callGetCheckDbVersion() throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCheckDBVer());
    }

    @Override
    public List<CardSetItemDTO> callGetCardSets() throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCardSets());
    }

    @Override
    public List<ArchetypesItemDTO> callGetArchetypes() throws Exception {
        return executeCall(client -> client.getDatabaseApi().getArchetypes());
    }

    @Override
    public CardSetInfo callGetCardSetInfo(GetCardSetInfoQueryParams params) throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCardSetInfo(params));
    }

    @Override
    public Card callGetRandomCard() throws Exception {
        return executeCall(client -> client.getDatabaseApi().getRandomCard());
    }

    @Override
    public CardInfoDTO callGetCardInfo(GetCardInfoQueryParams params) throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCardInfo(params));
    }

    @Override
    public CardInfoDTO callGetCardInfoWithMiscParam(GetCardInfoQueryParams params) throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCardInfo(params));
    }

    @Override
    public CardInfoDTO callGetCardInfoWithPagination(GetCardInfoQueryParams params) throws Exception {
        return executeCall(client -> client.getDatabaseApi().getCardInfo(params));
    }

    @Override
    public void callGetCardInfoWithWrongTypeParam(GetCardInfoQueryParams params) throws Exception {
        executeCall(client -> client.getDatabaseApi().getCardInfo(params), false);
    }
}
