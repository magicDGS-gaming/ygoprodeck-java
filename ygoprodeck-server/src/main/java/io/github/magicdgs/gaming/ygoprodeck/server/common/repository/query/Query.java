package io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dizitart.no2.objects.ObjectFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface Query {

    Query eq(final String key, final Object value);

    Query text(final String key, final String value);

    Query elemMatch(final String key, final String elemKey, final Object value);

    Query opPrefix(final String key, final String value);

    Query lte(String key, Object value);

    Query gte(String key, Object value);

    Query lt(String key, Object value);

    Query gt(String key, Object value);

    Query between(String key, Object first, Object second);

    Query eqIfNotNull(final String key, final Object value);

    Query textIfNotNull(final String key, final String value);

    Query elemMatchIfNotNull(final String key, final String elemKey, final Object value);

    Query opPrefixIfNotNull(final String key, final String value);

    Query betweenIfNotNull(String key, Object first, Object second);

    Query withSort(final Sort sort);

    Query withPageable(final Pageable pageable);

}
