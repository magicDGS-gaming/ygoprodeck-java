package io.github.magicdgs.gaming.ygoprodeck.server.database.impl;

import io.github.magicdgs.gaming.ygoprodeck.model.*;
import io.github.magicdgs.gaming.ygoprodeck.server.common.repository.query.Query;
import io.github.magicdgs.gaming.ygoprodeck.server.config.ApplicationConfig;
import io.github.magicdgs.gaming.ygoprodeck.server.database.DatabaseService;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.ArchetypeRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardSetInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.CardSetRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.DBVersionRepository;
import io.github.magicdgs.gaming.ygoprodeck.server.database.utils.UrlReplacer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
@Profile("!" + ApplicationConfig.PROXY_PROFILE)
public class RepositoryDatabaseService implements DatabaseService {
    private final ArchetypeRepository archetypeRepository;
    private final CardRepository cardRepository;
    private final CardSetRepository cardSetRepository;
    private final DBVersionRepository dbVersionRepository;

    @Override
    public ResponseEntity<List<ArchetypesItemDTO>> getArchetypes() throws Exception {
        return ResponseEntity.ok()
                .body(archetypeRepository.findAll());
    }

    private String changePageUrl(final long offset) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("offset", offset)
                .toUriString();
    }

    @Override
    public ResponseEntity<CardInfoDTO> getCardInfo(GetCardInfoQuery query) throws Exception {
        final Sort sort = toSort(query);
        final Pageable pageable = toPageable(query, sort);
        final boolean removeMisc = !YesSwitch.YES.equals(query.misc());
        final Optional<UriComponents> uriComponents = UrlReplacer.getServerUriComponents();
        final CardInfoQuery repoQuery = new CardInfoQuery(query);
        final CardInfoDTO body = new CardInfoDTO();
        if (pageable.isPaged()) {
            final Page<Card> page = cardRepository.findAll(repoQuery, pageable)
                            .map(card -> mapCard(card, uriComponents, removeMisc));
            uriComponents.ifPresent(uc -> page.map(card -> UrlReplacer.replaceUrl(card, uc)));
            body.setData(page.getContent());
            body.setMeta(toPagination(page));
        } else {
            final List<Card> cards = cardRepository.findAll(repoQuery, sort).stream()
                    .map(card -> mapCard(card, uriComponents, removeMisc))
                    .toList();
            body.setData(cards);
        }
        return ResponseEntity.ok().body(body);
    }

    private Query toQuery(GetCardInfoQuery cardInfoQuery) {
        final Sort sort = toSort(cardInfoQuery);
        final Pageable pageable = toPageable(cardInfoQuery, sort);
        return cardRepository.createEmptyQuery()
                .withSort(sort)
                .withPageable(pageable)
                .eqIfNotNull(Card.JSON_PROPERTY_NAME, cardInfoQuery.name())
                .textIfNotNull(Card.JSON_PROPERTY_FRAME_TYPE, cardInfoQuery.fname())
                .eqIfNotNull(Card.JSON_PROPERTY_ID, cardInfoQuery.id())
                .elemMatchIfNotNull(Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_KONAMI_ID, cardInfoQuery.konamiId())
                // TODO: I don't like this OP-prefix logic on the Query method...
                .opPrefix(Card.JSON_PROPERTY_ATK, cardInfoQuery.atk())
                .opPrefix(Card.JSON_PROPERTY_DEF, cardInfoQuery.def())
                .opPrefix(Card.JSON_PROPERTY_LEVEL, cardInfoQuery.level())
                .eq(Card.JSON_PROPERTY_RACE, cardInfoQuery.race())
                .eq(Card.JSON_PROPERTY_ATTRIBUTE, cardInfoQuery.attribute())
                .eq(Card.JSON_PROPERTY_LINKVAL, cardInfoQuery.link())
                // TODO: I am not sure if this one is correct
                .eq(Card.JSON_PROPERTY_LINKMARKERS, cardInfoQuery.linkmarker())
                .eq(Card.JSON_PROPERTY_SCALE, cardInfoQuery.scale())
                // TODO: not sure if this is correct
                .eq(Card.JSON_PROPERTY_CARD_SETS, cardInfoQuery.cardset())
                .eq(Card.JSON_PROPERTY_ARCHETYPE, cardInfoQuery.archetype())
                .eq(Card.JSON_PROPERTY_BANLIST_INFO, cardInfoQuery.banlist())
                .elemMatchIfNotNull(Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_FORMATS, cardInfoQuery.format())
                .elemMatchIfNotNull(Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_STAPLE, cardInfoQuery.staple())
                .elemMatchIfNotNull(Card.JSON_PROPERTY_MISC_INFO, CardMisc.JSON_PROPERTY_HAS_EFFECT, hasEffectValue(cardInfoQuery))
                .between(getDateProperty(cardInfoQuery), cardInfoQuery.startdate(), cardInfoQuery.enddate());
    }

    private String getDateProperty(GetCardInfoQuery cardInfoQuery) {
        return cardInfoQuery.dateregion() == null
                ? DateRegion.TCG_DATE.getValue()
                : cardInfoQuery.dateregion().getValue();
    }

    private Integer hasEffectValue(final GetCardInfoQuery value) {
        if (value.hasEffect() == null) {
            return null;
        }
        return value.hasEffect() ? 1 : 0;
    }

    private Card mapCard(final Card card, final Optional<UriComponents> uriComponents, boolean removeMisc) {
        if (removeMisc) {
            card.setMiscInfo(null);
        }
        uriComponents.ifPresent(uc -> UrlReplacer.replaceUrl(card, uc));
        return card;
    }

    private Sort toSort(GetCardInfoQuery query) {
        if (query.sort() != null) {
            return Sort.by(query.sort().getValue());
        }
        return Sort.unsorted();
    }


    private Pageable toPageable(final GetCardInfoQuery query, final Sort sort) {
        // TODO: also throw if invalid parameters
        if (query.num() != null && query.offset() != null) {
           return PageRequest.of(query.offset(), query.num(), sort);
        }
        return Pageable.unpaged();
    }

    private Pagination toPagination(final Page<?> page) {
        final Pagination pagination = new Pagination();
        pagination.setTotalRows((int) page.getTotalElements());
        pagination.setCurrentRows(page.getSize());
        pagination.setRowsRemaining((int) page.getTotalElements() - page.getSize());
        pagination.setTotalPages(page.getTotalPages());
        pagination.setPagesRemaining(page.getTotalPages() - page.getPageable().getPageNumber());
        if (page.hasNext()) {
            final ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
            pagination.setNextPageOffset((int) page.nextPageable().getOffset());
            pagination.setNextPage(changePageUrl(page.nextPageable().getOffset()));
        }
        /** TODO: document on openapi and do it
        if (page.hasPrevious()) {
            pagination.setPreviousPage((int) page.nextPageable().getOffset());
            pagination.setPreviousPageOffset(changePageUrl(page.nextPageable().getOffset()));
        }
         **/
        return pagination;
    }

    @Override
    public ResponseEntity<CardSetInfo> getCardSetInfo(GetCardSetInfoQuery query) throws Exception {
        final Page<Card> cards = cardRepository.findAll(new CardSetInfoQuery(query), Pageable.unpaged());
        if (cards.getSize() != 1) {
            // at least one should be found!
            // TODO: better error handling
            throw new IllegalArgumentException("Not found");
        }
        final Card card = cards.getContent().get(0);
        final CardSetInfo cardSetInfo = card.getCardSets()
                .stream()
                .filter(cardSet -> query.setcode().equals(cardSet.getSetCode()))
                .findFirst().orElseThrow();
        cardSetInfo.setId(card.getId());

        return ResponseEntity.ok()
                .body(cardSetInfo);
    }

    @Override
    public ResponseEntity<List<CardSetItemDTO>> getCardSets() throws Exception {
        final List<CardSetItemDTO> cardSets = cardSetRepository.findAll();
        UrlReplacer.getServerUriComponents().ifPresent(uc -> cardSets.forEach(card -> UrlReplacer.replaceUrl(card, uc)));
        return ResponseEntity.ok()
               .body(cardSets);
    }

    @Override
    public ResponseEntity<Card> getRandomCard() throws Exception {
        final long size = cardRepository.count();
        final long randomOffset = new Random().longs(1, size)
                .findFirst()
                .getAsLong();
        return ResponseEntity.ok()
                .body(cardRepository.findById(randomOffset).orElseThrow());
    }

    @Override
    public ResponseEntity<List<CheckDBVersionDTO>> getCheckDBVer() throws Exception {
        return ResponseEntity.ok()
                .body(dbVersionRepository.findAll());
    }
}
