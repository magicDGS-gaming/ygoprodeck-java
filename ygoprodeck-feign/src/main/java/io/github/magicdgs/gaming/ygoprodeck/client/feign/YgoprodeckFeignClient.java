package io.github.magicdgs.gaming.ygoprodeck.client.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import io.github.magicdgs.gaming.ygoprodeck.api.feign.ApiResponseDecoder;
import io.github.magicdgs.gaming.ygoprodeck.api.feign.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.api.feign.ImagesApi;
import io.github.magicdgs.gaming.ygoprodeck.api.feign.YgoprodeckErrorDecoder;
import io.github.magicdgs.gaming.ygoprodeck.model.exception.YgoprodeckException;
import io.github.magicdgs.gaming.ygoprodeck.utils.okhttp.OkhttpClientBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;

/**
 * Feign client for the YGOProDeck API containing the different APIs to be used.
 * </br>
 * This class is created with the {@link Builder} on this class.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class YgoprodeckFeignClient {

    @Getter
    private final DatabaseApi databaseApi;
    @Getter
    private final ImagesApi imagesApi;

    /**
     * Builder for the API client.
     */
    public static class Builder extends OkhttpClientBuilder<YgoprodeckFeignClient> {
        @Override
        protected YgoprodeckFeignClient doBuildInstance(final ObjectMapper objectMapper,
                                                        final OkHttpClient client)
                throws YgoprodeckException {

            final Feign.Builder clientBuilder = new Feign.Builder() //
                    .client(new feign.okhttp.OkHttpClient(client)) //
                    .errorDecoder(new YgoprodeckErrorDecoder(objectMapper)) //
                    .encoder(new JacksonEncoder(objectMapper)) //
                    .decoder(new ApiResponseDecoder(objectMapper)) //
                    .logger(new Slf4jLogger());
            return new YgoprodeckFeignClient(
                    clientBuilder.target(DatabaseApi.class, databaseUrl),
                    clientBuilder.target(ImagesApi.class, databaseUrl)
            );
        }

    }
}
