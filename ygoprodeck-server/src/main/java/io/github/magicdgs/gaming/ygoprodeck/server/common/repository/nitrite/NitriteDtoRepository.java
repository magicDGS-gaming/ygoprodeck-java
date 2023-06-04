package io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.ObjectRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@NoRepositoryBean
public class NitriteDtoRepository<T> implements DtoRepository<T> {
    private final Nitrite nitriteDb;
    private final Class<T> type;

    protected final Class<T> getType() {
        return type;
    }

    protected final ObjectRepository<T> getObjectRepository() {
        return nitriteDb.getRepository(getType());
    }

    @Override
    public final <S extends T> S save(S entity) {
        getObjectRepository().insert(entity);
        return entity;
    }
    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        final S[] asArray = (S[]) StreamSupport
                .stream(entities.spliterator(), false)
                .toArray();
        getObjectRepository().insert(asArray);
        return Arrays.asList(asArray);
    }

    @Override
    public final List<T> findAll() {
        return getObjectRepository().find().toList();
    }

    @Override
    public final Optional<T> findById(Long id) {
        final T dto = getObjectRepository().getById(NitriteId.createId(id));
        return Optional.ofNullable(dto);
    }

    @Override
    public final long count() {
        return getObjectRepository().size();
    }

    @Override
    public void deleteAll() {
        getObjectRepository().drop();
    }
}
