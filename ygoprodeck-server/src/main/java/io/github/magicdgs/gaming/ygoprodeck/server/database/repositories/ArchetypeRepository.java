package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories;

import io.github.magicdgs.gaming.ygoprodeck.model.ArchetypesItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ArchetypeRepository extends DtoRepository<ArchetypesItemDTO> {
}
