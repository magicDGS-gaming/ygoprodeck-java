package io.github.magicdgs.gaming.ygoprodeck.server.common;

import io.github.magicdgs.gaming.ygoprodeck.api.retrofit.DatabaseApi;
import io.github.magicdgs.gaming.ygoprodeck.api.retrofit.YgoprodeckApiResultCallback;
import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.ArchetypeRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardSetRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.DBVersionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static io.github.magicdgs.gaming.ygoprodeck.utils.DatabaseApiQueryUtils.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public final class DatabaseReloader {

    // client
    private final YgoprodeckRetrofitClient client;
    // repositories
    private final DBVersionRepository dbVersionRepository;
    private final ArchetypeRepository archetypeRepository;
    private final CardRepository cardRepository;
    private final CardSetRepository cardSetRepository;

    public boolean shouldReload() {
        // TODO: implement logic for checking the current and the old DB
        // TODO: currently only checks if there is at least one DB version stored
        return !dbVersionRepository.findAll().isEmpty();
    }

    public void reloadDatabase() {
        reloadDatabase(false);
    }

    public void reloadDatabase(boolean force) {
        log.info("Reloading database");
        if (force || !shouldReload()) {
            log.info("Fetching data");
            // load all data from all endpoints
            var cardInfoDto = loadFromApiAsync(dbApi -> dbApi.getCardInfo(
                    new GetCardInfoQueryParams()
                            .misc(YesSwitch.YES)));
            var cardSetInfo = loadFromApiAsync(DatabaseApi::getCardSets);
            var archetypes = loadFromApiAsync(DatabaseApi::getArchetypes);
            var dbVersionResponse = loadFromApiAsync(DatabaseApi::getCheckDBVer);
            // wait for the calls and catch any error
            final List<ErrorDTO> errors = awaitAllAndCollectErrors(dbVersionResponse, archetypes, cardInfoDto, cardSetInfo);
            if (!errors.isEmpty()) {
                // TODO: this should be better handled
                throw new RuntimeException(errors.toString());
            }
            // otherwise, save in the repository all the data after deleting it
            log.info("Delete outdated data");
            dropDatabase();
            log.info("Persisting data");
            cardRepository.saveAll(cardInfoDto.getResult().get().getData());
            cardSetRepository.saveAll(cardSetInfo.getResult().get());
            archetypeRepository.saveAll(archetypes.getResult().get());
            dbVersionRepository.saveAll(dbVersionResponse.getResult().get());
        }
        log.info("Database reloaded");
    }

    private void dropDatabase() {
        dbVersionRepository.deleteAll();
        cardRepository.deleteAll();
        archetypeRepository.deleteAll();
        cardSetRepository.deleteAll();
    }

    private <T> YgoprodeckApiResultCallback<T> loadFromApiAsync(final Function<DatabaseApi, Call<T>> apiFunction) {
        final DatabaseApi dbApi = client.getDatabaseApi();
        final YgoprodeckApiResultCallback<T> resultCallback = new YgoprodeckApiResultCallback<>();
        apiFunction.apply(dbApi).enqueue(resultCallback);
        return resultCallback;
    }

    private List<ErrorDTO> awaitAllAndCollectErrors(YgoprodeckApiResultCallback<?>... apiResultCallbacks) {
        try {
            final List<ErrorDTO> errorAccumulator = new ArrayList<>(apiResultCallbacks.length);
            for (final YgoprodeckApiResultCallback<?> callback: apiResultCallbacks) {
                callback.awaitResult();
                callback.getError().ifPresent(errorAccumulator::add);
            }
            return errorAccumulator;
        } catch (final InterruptedException e) {
            final ErrorDTO errorDto = new ErrorDTO();
            errorDto.setError("Service interrupted while fetching data");
            log.error(errorDto.getError(), e);
            return Collections.singletonList(errorDto);
        }
    }

}
