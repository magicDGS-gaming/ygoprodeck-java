package io.github.magicdgs.gaming.ygoprodeck.server.database.impl;

import java.util.List;

import io.github.magicdgs.gaming.ygoprodeck.client.retrofit.YgoprodeckRetrofitClient;
import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;

import io.github.magicdgs.gaming.ygoprodeck.server.database.DatabaseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Profile(ApplicationConfig.PROXY_PROFILE)
public class ProxyDatabaseService implements DatabaseService {
    private final YgoprodeckRetrofitClient client;

    @Override
    public ResponseEntity<List<ArchetypesItemDTO>> getArchetypes() throws Exception {
        final var dto = client.getDatabaseApi().getArchetypes().execute().body();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<CardInfoDTO> getCardInfo(GetCardInfoQuery query) throws Exception {
        final var dto = client.getDatabaseApi().getCardInfo(query.toQueryParams()).execute().body();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<CardSetInfo> getCardSetInfo(GetCardSetInfoQuery query) throws Exception {
        final var dto = client.getDatabaseApi().getCardSetInfo(query.toQueryParams()).execute().body();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<CardSetItemDTO>> getCardSets() throws Exception {
        final var dto = client.getDatabaseApi().getCardSets().execute().body();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<CheckDBVersionDTO>> getCheckDBVer() throws Exception {
        final var dto = client.getDatabaseApi().getCheckDBVer().execute().body();
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Card> getRandomCard() throws Exception {
        final var dto = client.getDatabaseApi().getRandomCard().execute().body();
        return ResponseEntity.ok(dto);
    }
}
