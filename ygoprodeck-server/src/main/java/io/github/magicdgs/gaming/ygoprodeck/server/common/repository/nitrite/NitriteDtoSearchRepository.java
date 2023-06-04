package io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.DtoSearchRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@NoRepositoryBean
public abstract class NitriteDtoSearchRepository<T, Q>
        extends NitriteDtoRepository<T>
        implements DtoSearchRepository<T, Q> {

    protected NitriteDtoSearchRepository(Nitrite nitriteDb, Class<T> type) {
        super(nitriteDb, type);
    }

    @PostConstruct
    protected abstract void createIndexes();

    @Override
    public Query createEmptyQuery() {
        return new NitriteQuery();
    }

    @Override
    public void deleteAll() {
        super.deleteAll();
        // re-index if drop completely
        createIndexes();
    }

    protected abstract ObjectFilter toObjectFilter(final Q query);

    @Override
    public Page<T> findAll(Query query) {
        return null;
    }

    @Override
    public List<T> findAll(Q query, Sort sort) {
        final FindOptions sortOptions = toSortOptions(sort);
        final ObjectFilter objectFilter = toObjectFilter(query);
        return getObjectRepository().find(objectFilter, sortOptions).toList();
    }

    @Override
    public final Page<T> findAll(Q query, Pageable pageable) {
        final List<T> content;
        final ObjectFilter objectFilter = toObjectFilter(query);
        if (pageable.isUnpaged()) {
            content = getObjectRepository().find(objectFilter).toList();
        } else {
            final FindOptions findOptions = toFindOptions(pageable);
            content = getObjectRepository().find(objectFilter, findOptions).toList();
        }
        return new PageImpl<>(content, pageable, count());
    }

    private FindOptions toFindOptions(final Pageable pageable) {
        final FindOptions sortOptions = toSortOptions(pageable.getSort());
        if (pageable.isUnpaged()) {
            return sortOptions;
        }
        return sortOptions.thenLimit(
                    (int) pageable.getOffset(),
                    pageable.getPageSize());
    }

    private FindOptions toSortOptions(final Sort sort) {
        if (sort != null || sort.isUnsorted()) {
            return FindOptions.sort("_id", (SortOrder.Ascending));
        }
        // TODO: can we support more than one sorting with nitrite?
        return sort.get().findFirst()
                .map(o -> FindOptions.sort(
                        o.getProperty(),
                        toSortOrder(o.getDirection())))
                .get();
    }

    private SortOrder toSortOrder(final Sort.Direction direction) {
        return switch (direction) {
            case ASC -> SortOrder.Ascending;
            case DESC -> SortOrder.Descending;
        };
    }

    private ObjectFilter createEqFilter(final Object queryParam, final String property) {
        if (queryParam != null) {
            if (queryParam instanceof List<?> listQueryParam) {
                var filters = listQueryParam.stream()
                        .map(qp -> ObjectFilters.eq(property, qp))
                        .toArray(ObjectFilter[]::new);
                return ObjectFilters.or(filters);
            } else {
                return ObjectFilters.eq(property, queryParam);
            }
        }
        return null;
    }

}
