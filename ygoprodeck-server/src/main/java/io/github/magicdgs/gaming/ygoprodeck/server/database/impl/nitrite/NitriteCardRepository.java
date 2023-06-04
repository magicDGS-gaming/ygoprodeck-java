package io.github.magicdgs.gaming.ygoprodeck.server.database.impl.nitrite;

import io.github.magicdgs.gaming.ygoprodeck.model.Card;
import io.github.magicdgs.gaming.ygoprodeck.model.CardMisc;
import io.github.magicdgs.gaming.ygoprodeck.model.CardSetInfo;
import io.github.magicdgs.gaming.ygoprodeck.model.DateRegion;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.nitrite.NitriteDtoSearchRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query.Query;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardSetInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApiDelegate.*;

@Repository
@Slf4j
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class NitriteCardRepository
        extends NitriteDtoSearchRepository<Card, CardQuery>
        implements CardRepository {
    public NitriteCardRepository(Nitrite nitriteDb) {
        super(nitriteDb, Card.class);
    }

    @Override
    protected void createIndexes() {
        if (!getObjectRepository().hasIndex(Card.JSON_PROPERTY_NAME)) {
            log.info("Indexing " + Card.JSON_PROPERTY_NAME);
            getObjectRepository().createIndex(
                    Card.JSON_PROPERTY_NAME,
                    IndexOptions.indexOptions(IndexType.Fulltext, false));
        }
    }

    @Override
    protected ObjectFilter toObjectFilter(CardQuery query) {
        if (query instanceof CardInfoQuery infoQuery) {
            return toObjectFilter(infoQuery.query());
        } else if (query instanceof CardSetInfoQuery setInfoQuery) {
            return toObjectFilter(setInfoQuery.query());
        }
        throw new IllegalArgumentException("Unknown query");
    }

    private ObjectFilter toObjectFilter(final GetCardSetInfoQuery query) {
        return createNestedArrayFilter(query.setcode(), Card.JSON_PROPERTY_CARD_SETS, CardSetInfo.JSON_PROPERTY_SET_CODE);
    }

    private ObjectFilter toObjectFilter(final GetCardInfoQuery query) {
        final List<ObjectFilter> accumulator = new ArrayList<>();
        // TODO: check if it is enough to have the property or not
        eq(query.name(), Card.JSON_PROPERTY_NAME, accumulator);
        text(query.fname(), Card.JSON_PROPERTY_NAME, accumulator);
        eq(query.id(), Card.JSON_PROPERTY_ID, accumulator);
        addFilterNestedArray(query.konamiId(), Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_KONAMI_ID, accumulator);
        eq(query.type(), Card.JSON_PROPERTY_TYPE, accumulator);
        addFilterNumeric(query.atk(), Card.JSON_PROPERTY_ATK, accumulator);
        addFilterNumeric(query.def(), Card.JSON_PROPERTY_DEF, accumulator);
        addFilterNumeric(query.level(), Card.JSON_PROPERTY_LEVEL, accumulator);
        eq(query.race(), Card.JSON_PROPERTY_RACE, accumulator);
        eq(query.race(), Card.JSON_PROPERTY_ATTRIBUTE, accumulator);
        eq(query.link(), Card.JSON_PROPERTY_LINKVAL, accumulator);
        // TODO: I am not sure if this one is correct
        eq(query.linkmarker(), Card.JSON_PROPERTY_LINKMARKERS, accumulator);
        eq(query.scale(), Card.JSON_PROPERTY_SCALE, accumulator);
        eq(query.cardset(), Card.JSON_PROPERTY_CARD_SETS, accumulator);
        eq(query.archetype(), Card.JSON_PROPERTY_ARCHETYPE, accumulator);
        eq(query.banlist(), Card.JSON_PROPERTY_BANLIST_INFO, accumulator);
        addFilterNestedArray(query.format(), Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_FORMATS, accumulator);
        addFilterNestedArray(query.staple(), Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_STAPLE, accumulator);
        addFilterNestedArray(hasEffectValue(query), Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_HAS_EFFECT, accumulator);
        addFilterDate(query.startdate(), query.enddate(), query.dateregion(), accumulator);
        if (accumulator.isEmpty()) {
            return ObjectFilters.ALL;
        } else {
            return ObjectFilters.and(accumulator.toArray(ObjectFilter[]::new));
        }
    }

    private Integer hasEffectValue(final GetCardInfoQuery value) {
        if (value.hasEffect() == null) {
            return null;
        }
        return value.hasEffect() ? 1 : 0;
    }

    private void addFilterDate(LocalDate startdate, LocalDate enddate, DateRegion dateregion,
                               List<ObjectFilter> accumulator) {
        if (startdate != null && enddate != null) {
            final String property = dateregion == null
                    ? DateRegion.TCG_DATE.getValue()
                    : dateregion.getValue();
            var filter = ObjectFilters.and(
                    ObjectFilters.gte(property, startdate),
                    ObjectFilters.lte(property, enddate));
            accumulator.add(filter);
        }
    }

    private void addFilterNumeric(String queryParam, String property, List<ObjectFilter> accumulator) {
        if (queryParam != null) {
            try {
                final int value = Integer.valueOf(queryParam);
                eq(value, property, accumulator);
            } catch (NumberFormatException e) {
                //TODO: there are for sure other options for this
                final String operation = Arrays.stream(new String[]{"lte", "gte", "lt", "gt"})
                        .filter(queryParam::startsWith)
                        .findFirst().orElseThrow();
                final Integer value = Integer.valueOf(queryParam.substring(operation.length()));
                ObjectFilter filter = switch (operation) {
                    case "lte" -> ObjectFilters.lte(property, value);
                    case "gte" -> ObjectFilters.gte(property, value);
                    case "lt" -> ObjectFilters.lt(property, value);
                    case "gt" -> ObjectFilters.gt(property, value);
                    default -> throw new UnsupportedOperationException("Not implemented yet:" + queryParam);
                };
                accumulator.add(filter);
            }
        }
    }

    // TODO: I am not sure if this one is correct
    private void addFilterNestedArray(Object queryParam, String arrayProperty, String property, List<ObjectFilter> accumulator) {
        final ObjectFilter filter = createNestedArrayFilter(queryParam, arrayProperty, property);
        if (filter != null) {
            accumulator.add(filter);
        }
    }

    private void text(final String queryParam, final String property, final List<ObjectFilter> accumulator) {
        if (queryParam != null) {
            final var filter = ObjectFilters.text(property, queryParam);
            accumulator.add(filter);
        }
    }

    private void eq(final Object queryParam, final String property, final List<ObjectFilter> accumulator) {
        final ObjectFilter filter = createEqFilter(queryParam, property);
        if (filter != null) {
            accumulator.add(filter);
        }
    }

    private ObjectFilter createNestedArrayFilter(Object queryParam, String arrayProperty, String property) {
        final ObjectFilter filter = createEqFilter(queryParam, property);
        if (filter != null) {
            return ObjectFilters.elemMatch(arrayProperty, filter);
        }
        return null;
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
