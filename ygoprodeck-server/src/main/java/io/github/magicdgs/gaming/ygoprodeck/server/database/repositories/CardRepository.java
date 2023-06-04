package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories;

import io.github.magicdgs.gaming.ygoprodeck.model.Card;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoSearchRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardQuery;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CardRepository extends DtoSearchRepository<Card, CardQuery> {
}
