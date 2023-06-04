package io.github.magicdgs.gaming.ygoprodeck.server.database.impl.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.model.ArchetypesItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite.NitriteDtoRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.ArchetypeRepository;
import org.dizitart.no2.Nitrite;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class NitriteArchetypeRepository
        extends NitriteDtoRepository<ArchetypesItemDTO>
        implements ArchetypeRepository {

    public NitriteArchetypeRepository(Nitrite nitriteDb) {
        super(nitriteDb, ArchetypesItemDTO.class);
    }
}
