package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories;

import io.github.magicdgs.gaming.ygoprodeck.model.CheckDBVersionDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface DBVersionRepository extends DtoRepository<CheckDBVersionDTO> {
}
