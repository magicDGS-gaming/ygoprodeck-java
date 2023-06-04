package io.github.magicdgs.gaming.ygoprodeck.server.common.repository;

import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface DtoSearchRepository<T, Q> extends DtoRepository<T> {

    Query createEmptyQuery();

    Page<T> findAll(final Query query);

    @Deprecated
    Page<T> findAll(Q query, Pageable pageable);

    @Deprecated
    List<T> findAll(Q query, Sort sort);

}
