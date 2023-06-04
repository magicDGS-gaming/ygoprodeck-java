package io.github.magicdgs.gaming.ygoprodeck.server.database.impl.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.model.CardSetItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite.NitriteDtoRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardSetRepository;
import org.dizitart.no2.Nitrite;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class NitriteCardSetRepository
        extends NitriteDtoRepository<CardSetItemDTO>
        implements CardSetRepository {
    protected NitriteCardSetRepository(Nitrite nitriteDb) {
        super(nitriteDb, CardSetItemDTO.class);
    }
}
