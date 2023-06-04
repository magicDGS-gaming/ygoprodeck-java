package io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query;

import io.github.magicdgs.gaming.ygoprodeck.server.DatabaseApiDelegate.GetCardSetInfoQuery;
import io.github.magicdgs.gaming.ygoprodeck.server.database.repositories.query.CardQuery;

public record CardSetInfoQuery(GetCardSetInfoQuery query) implements CardQuery {
}
