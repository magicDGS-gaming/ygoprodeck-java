package io.github.magicdgs.gaming.ygoprodeck.server.database.impl.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.model.CheckDBVersionDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite.NitriteDtoRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.DBVersionRepository;
import org.dizitart.no2.Nitrite;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Profile("!" + ApplicationConfig.PROXY_PROFILE)
@Repository
public class NitriteCheckDBVersionDTORepository
        extends NitriteDtoRepository<CheckDBVersionDTO>
        implements DBVersionRepository {
    protected NitriteCheckDBVersionDTORepository(Nitrite nitriteDb) {
        super(nitriteDb, CheckDBVersionDTO.class);
    }
}
