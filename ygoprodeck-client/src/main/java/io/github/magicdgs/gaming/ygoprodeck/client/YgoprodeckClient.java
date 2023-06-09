package io.github.magicdgs.gaming.ygoprodeck.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.magicdgs.gaming.ygoprodeck.api.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.api.ImagesApi;
import io.github.magicdgs.gaming.ygoprodeck.client.internal.retrofit.YgoprodeckExceptionCallAdapterFactory;
import io.github.magicdgs.gaming.ygoprodeck.client.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.client.internal.okhttp.OkhttpClientBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Retrofit client for the YGOProDeck API containing the different APIs to be used.
 * </br>
 * This class is created with the {@link Builder} on this class.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class YgoprodeckClient {

    @Getter
    private final DatabaseApi databaseApi;
    @Getter
    private final ImagesApi imagesApi;

    /**
     * Builder for the API client.
     */
    public static class Builder extends OkhttpClientBuilder<YgoprodeckClient> {

        @Override
        protected YgoprodeckClient doBuildInstance(ObjectMapper objectMapper, OkHttpClient client) throws YgoprodeckException {
            final Retrofit.Builder clientBuilder = new Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(YgoprodeckExceptionCallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(JacksonConverterFactory.create(objectMapper));
            return new YgoprodeckClient(
                    clientBuilder.baseUrl(databaseUrl).build().create(DatabaseApi.class),
                    clientBuilder.baseUrl(imagesUrl).build().create(ImagesApi.class)
            );
        }
    }
}
