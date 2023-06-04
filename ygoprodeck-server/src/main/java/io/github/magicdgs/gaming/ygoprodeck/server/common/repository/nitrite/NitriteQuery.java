package io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query.Query;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class NitriteQuery implements Query {

    final List<ObjectFilter> objectFilters = new ArrayList<>();
    // TODO: we need to check how to give priority to sort/pageable
    // TODO: or rather ignore the sort options on the pageable
    FindOptions findOptions;

    @Override
    public Query eq(String key, Object value) {
        objectFilters.add(eqFilter(key, value));
        return this;
    }

    private ObjectFilter eqFilter(final String key, final Object value) {
        if (value instanceof List<?> listQueryParam) {
            var filters = listQueryParam.stream()
                    .map(qp -> ObjectFilters.eq(key, qp))
                    .toArray(ObjectFilter[]::new);
            return ObjectFilters.or(filters);
        }
        return ObjectFilters.eq(key, value);
    }

    public Query text(String key, String value) {
        final var filter = ObjectFilters.text(key, value);
        objectFilters.add(filter);
        return this;
    }

    @Override
    public Query elemMatch(String key, String elemKey, Object value) {
        final ObjectFilter filter = ObjectFilters.elemMatch(key, eqFilter(elemKey, value));
        objectFilters.add(filter);
        return this;
    }

    @Override
    public Query opPrefix(String key, String value) {
        // TODO: not sure if should conver in numeric?
        if (value.startsWith("lte")) {
            return lte(key, value.substring(3));
        } else if (value.startsWith("gte")) {
            return gte(key, value.substring(3));
        } else if (value.startsWith("lt")) {
            return lt(key, value.substring(2));
        } else if (value.startsWith("gt")) {
            return gt(key, value.substring(2));
        } else {
            return eq(key, value);
        }
    }

    @Override
    public Query lte(String key, Object value) {
        objectFilters.add(ObjectFilters.lte(key, value));
        return this;
    }

    @Override
    public Query gte(String key, Object value) {
        objectFilters.add(ObjectFilters.gte(key, value));
        return this;
    }

    @Override
    public Query lt(String key, Object value) {
        objectFilters.add(ObjectFilters.lt(key, value));
        return this;
    }

    @Override
    public Query gt(String key, Object value) {
        objectFilters.add(ObjectFilters.gt(key, value));
        return this;
    }

    @Override
    public Query between(String key, Object first, Object second) {
        final ObjectFilter filter = ObjectFilters.and(
                ObjectFilters.gte(key, first),
                ObjectFilters.lte(key, second));
        objectFilters.add(filter);
        return null;
    }

    public Query eqIfNotNull(String key, Object  value) {
        if (value != null) {
            return eq(key, value);
        }
        return this;
    }

    @Override
    public Query textIfNotNull(String key, String value) {
        if (value != null) {
            return text(key, value);
        }
        return this;
    }

    @Override
    public Query elemMatchIfNotNull(String key, String elemKey, Object value) {
        if (value != null) {
            elemMatch(key, elemKey, value);
        }
        return null;
    }

    @Override
    public Query opPrefixIfNotNull(String key, String value) {
        if (value != null) {
            opPrefix(key, value);
        }
        return this;
    }

    @Override
    public Query betweenIfNotNull(String key, Object first, Object second) {
        if (first != null || second != null) {
            if (first == null) {
                lte(key, second);
            } else if (second == null) {
                gte(key, first);
            } else {
                between(key, first, second);
            }
        }
        return this;
    }

    @Override
    public Query withSort(Sort sort) {
        findOptions = toSortOptions(sort);
        return this;
    }

    @Override
    public Query withPageable(Pageable pageable) {
        findOptions = toFindOptions(pageable);
        return this;
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



    private FindOptions toFindOptions(final Pageable pageable) {
        final FindOptions sortOptions = toSortOptions(pageable.getSort());
        if (pageable.isUnpaged()) {
            return sortOptions;
        }
        return sortOptions.thenLimit(
                (int) pageable.getOffset(),
                pageable.getPageSize());
    }
}
