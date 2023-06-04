package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories;

import io.github.magicdgs.gaming.ygoprodeck.model.CardSetItemDTO;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CardSetRepository extends DtoRepository<CardSetItemDTO> {
}
