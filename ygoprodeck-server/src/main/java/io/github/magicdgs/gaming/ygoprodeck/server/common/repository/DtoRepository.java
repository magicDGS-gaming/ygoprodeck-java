package io.github.magicdgs.gaming.ygoprodeck.server.common.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface DtoRepository<T> extends Repository<T, Long> {
    <S extends T> S save(S entity);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    List<T> findAll();

    Optional<T> findById(Long id);

    long count();

    void deleteAll();

}
